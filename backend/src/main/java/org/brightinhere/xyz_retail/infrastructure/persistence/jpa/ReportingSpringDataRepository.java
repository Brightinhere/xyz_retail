package org.brightinhere.xyz_retail.infrastructure.persistence.jpa;

import org.brightinhere.xyz_retail.application.port.ReportingRepository;
import org.brightinhere.xyz_retail.domain.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReportingSpringDataRepository extends JpaRepository<OrderItem, UUID> {

    @Query("""
            SELECT
                p.name AS productName,
                p.price AS price,
                i.quantity AS quantityAvailable,
                SUM(oi.quantity) AS quantitySold
            FROM OrderItem oi
            JOIN oi.order o
            JOIN oi.product p
            JOIN p.inventory i
            WHERE DATE(o.createdAt) = :day
              AND o.status = 'PLACED'
            GROUP BY p.name, p.price, i.quantity
            ORDER BY SUM(oi.quantity) DESC
            """)
    List<ReportingRepository.ProductSalesRow> topSellingProductsOfDay(
            @Param("day") LocalDate day,
            Pageable pageable
    );

    @Query("""
                SELECT
                    p.name AS productName,
                    p.price AS price,
                    i.quantity AS quantityAvailable,
                    COALESCE(SUM(oi.quantity), 0) AS quantitySold
                FROM Product p
                JOIN p.inventory i
                LEFT JOIN OrderItem oi ON oi.product = p
                LEFT JOIN oi.order o
                     ON o.status = 'PLACED'
                    AND o.createdAt >= :start
                    AND o.createdAt < :end
                GROUP BY p.name, p.price, i.quantity
                ORDER BY quantitySold ASC
            """)
    List<ReportingRepository.ProductSalesRow> leastSellingProductsOfMonth(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

    @Query("""
            SELECT
                DATE(o.createdAt) AS day,
                SUM(o.totalAmount) AS totalSales
            FROM Order o
            WHERE o.status = 'PLACED'
              AND DATE(o.createdAt) BETWEEN :start AND :end
            GROUP BY DATE(o.createdAt)
            ORDER BY DATE(o.createdAt)
            """)
    List<ReportingRepository.SalesPerDayRow> saleAmountPerDay(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}