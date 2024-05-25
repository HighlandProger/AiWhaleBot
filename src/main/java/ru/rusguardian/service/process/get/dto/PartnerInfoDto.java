package ru.rusguardian.service.process.get.dto;

import lombok.Data;
import ru.rusguardian.constant.user.PartnerLevel;

@Data
public class PartnerInfoDto {
    private double balance;

    private PartnerLevel level;
    private double partnerPercent;
    private int invitedCount;
    private long subscriptionPurchasesCount;
    private long extraPurchasesCount;
    private double referralPurchasesSum;
    private double partnerIncome;

}
