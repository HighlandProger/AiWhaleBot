package ru.rusguardian.domain;

import jakarta.persistence.*;
import lombok.*;
import ru.rusguardian.constant.purchase.PurchaseProvider;
import ru.rusguardian.constant.purchase.SeparatePurchase;
import ru.rusguardian.constant.user.SubscriptionType;

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
    private float price;
    @Enumerated(EnumType.STRING)
    @Column(name = "purchase_provider")
    private PurchaseProvider purchaseProvider;
    @Enumerated(EnumType.STRING)
    @Column
    private SeparatePurchase separatePurchase;
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_type")
    private SubscriptionType subscriptionType;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "purchased_at")
    private LocalDateTime purchasedAt;
    @Column(name = "invoice_url")
    private String invoiceUrl;
    @Column(name = "is_purchased")
    private boolean isPurchased;

    public Order(PurchaseProvider purchaseProvider, SeparatePurchase separatePurchase) {
        this.info = separatePurchase.name();
        this.price = separatePurchase.getPrice();
        this.purchaseProvider = purchaseProvider;
        this.separatePurchase = separatePurchase;
        this.createdAt = LocalDateTime.now();
        this.isPurchased = false;
    }

    public Order(PurchaseProvider provider, SubscriptionInfo subscriptionInfo) {
        this.info = subscriptionInfo.getName();
        this.price = subscriptionInfo.getPrice();
        this.purchaseProvider = provider;
        this.subscriptionType = subscriptionInfo.getType();
        this.createdAt = LocalDateTime.now();
        this.isPurchased = false;
    }


}
