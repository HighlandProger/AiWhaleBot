package ru.rusguardian.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.rusguardian.constant.purchase.PurchaseProvider;
import ru.rusguardian.domain.user.Chat;

import java.time.LocalDateTime;

@Entity
@Table(schema = "ncs_bot", name = "user_subscriptions")
@Getter
@Setter
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;
    @ManyToOne
    @JoinColumn(name = "subscription_type")
    private Subscription subscription;
    @Column(name = "start_time")
    private LocalDateTime startTime;
    @Column(name = "expiration_time")
    private LocalDateTime expirationTime;
    @Enumerated(EnumType.STRING)
    @Column(name = "purchase_provider")
    private PurchaseProvider purchaseProvider;
    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;


}
