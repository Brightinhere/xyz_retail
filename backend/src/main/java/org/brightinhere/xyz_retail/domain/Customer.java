package org.brightinhere.xyz_retail.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "customer")
@Getter
@Setter
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String mobileNumber;

    private String email;
}
