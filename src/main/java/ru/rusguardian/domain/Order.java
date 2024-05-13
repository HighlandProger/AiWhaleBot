package ru.rusguardian.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(schema = "ncs_bot", name = "orders")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String info;
    @Column
    private Double price;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "purchased_at")
    private LocalDateTime purchasedAt;
    @Column(name = "invoice_url")
    private String invoiceUrl;
    @Column(name = "is_purchased")
    private boolean isPurchased;

    public Order(String info, Double price) {
        this.info = info;
        this.price = price;
        this.createdAt = LocalDateTime.now();
        this.isPurchased = false;
    }
}
