package org.brightinhere.xyz_retail.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.brightinhere.xyz_retail.infrastructure.exception.InsufficientStockException;
import org.brightinhere.xyz_retail.infrastructure.exception.InvalidOrderStateException;

import java.util.UUID;

@Entity
@Table(name = "inventory")
@Getter
@Setter
public class Inventory extends BaseEntity {
    @Id
    private UUID productId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Version
    private Long version;

    public void deduct(int amount) {
        if (amount <= 0) {
            throw new InvalidOrderStateException("Amount must be positive");
        }

        if (this.quantity < amount) {
            throw new InsufficientStockException();
        }

        this.quantity -= amount;
    }
}