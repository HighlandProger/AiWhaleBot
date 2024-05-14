package ru.rusguardian.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import ru.rusguardian.domain.SubscriptionInfo;

import java.time.LocalDateTime;

@Embeddable
@Data
public class SubscriptionEmbedded {

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
