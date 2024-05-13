package ru.rusguardian.domain.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(schema = "ncs_bot", name = "subscriptions")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "subscription_info_id")
    private SubscriptionInfo subscriptionInfo;
    @Column(name = "purchase_time")
    private LocalDateTime purchaseTime;
    @Column(name = "expiration_date")
    private LocalDateTime expirationTime;
    @Column(name = "purchase_type")
    private String purchaseType;


}
