package ru.rusguardian.domain.user;

import jakarta.persistence.*;
import lombok.Data;
import ru.rusguardian.constant.user.PartnerLevel;
import ru.rusguardian.domain.converters.PartnerLevelConverter;

@Embeddable
@Data
public class PartnerEmbedded {

    @OneToOne
    @JoinColumn(name = "invited_by")
    private Chat invitedBy;
    @Convert(converter = PartnerLevelConverter.class)
    @Column(name = "partner_level")
    private PartnerLevel partnerLevel;
    @Column(name = "referrals_count")
    private int referralsCount;
    @Column(name = "referrals_subscriptions_purchase_count")
    private int referralsSubscriptionsPurchaseCount;
    @Column(name = "referrals_extra_purchase_count")
    private int referralsExtraPurchaseCount;
    @Column(name = "referrals_purchase_value")
    private double referralsPurchaseValue;
    @Column(name = "balance")
    private double balance;

}
