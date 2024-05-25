package ru.rusguardian.domain.user;

import jakarta.persistence.*;
import lombok.Data;
import ru.rusguardian.constant.purchase.PurchaseProvider;
import ru.rusguardian.domain.SubscriptionInfo;

import java.time.LocalDateTime;

@Embeddable
@Data
public class SubscriptionEmbedded {

    @ManyToOne
    @JoinColumn(name = "subscription_info_type")
    private SubscriptionInfo subscriptionInfo;
    @Column(name = "purchase_time")
    private LocalDateTime purchaseTime;
    @Column(name = "expiration_date")
    private LocalDateTime expirationTime;
    @Enumerated(EnumType.STRING)
    @Column(name = "purchase_type")
    private PurchaseProvider purchaseType;

}
