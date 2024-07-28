package ru.rusguardian.domain;

import jakarta.persistence.*;
import lombok.*;
import ru.rusguardian.constant.purchase.PurchaseProvider;
import ru.rusguardian.constant.purchase.SeparatePurchase;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.user.Chat;

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
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;
    @Column
    private String info;
    @Column
    private double price;
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
    @Column(name = "partner_income")
    private Double partnerIncome;
    @Column(name = "subscription_months")
    private Integer subscriptionMonths;

    public Order(Chat chat, PurchaseProvider purchaseProvider, SeparatePurchase separatePurchase) {
        this.info = separatePurchase.name();
        this.price = separatePurchase.getPrice();
        this.chat = chat;
        this.purchaseProvider = purchaseProvider;
        this.separatePurchase = separatePurchase;
        this.createdAt = LocalDateTime.now();
        this.isPurchased = false;
    }

    public Order(Chat chat, PurchaseProvider provider, Subscription subscription, int months) {
        this.info = subscription.getName();
        this.price = subscription.getOneMonthPrice() * months;
        this.chat = chat;
        this.purchaseProvider = provider;
        this.subscriptionType = subscription.getType();
        this.createdAt = LocalDateTime.now();
        this.isPurchased = false;
    }


}
