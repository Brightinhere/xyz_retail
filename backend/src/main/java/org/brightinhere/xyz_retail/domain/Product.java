package org.brightinhere.xyz_retail.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "product")
@Getter
@Setter
public class Product extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    private Inventory inventory;
}
