package org.brightinhere.xyz_retail.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.brightinhere.xyz_retail.infrastructure.exception.InvalidOrderStateException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = true)
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public int getTotalQuantity() {
        return items.stream().mapToInt(OrderItem::getQuantity).sum();
    }


    public void place() {
        if (this.items.isEmpty()) {
            throw new InvalidOrderStateException("Cannot place empty order");
        }

        if (this.status != OrderStatus.CREATED) {
            throw new InvalidOrderStateException("Order already processed");
        }

        this.status = OrderStatus.PLACED;
    }
}
