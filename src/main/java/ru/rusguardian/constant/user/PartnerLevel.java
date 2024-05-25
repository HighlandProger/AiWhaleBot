package ru.rusguardian.constant.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PartnerLevel {

    STANDARD("Стандартный", 15),
    SILVER("Серебряный", 20),
    GOLD("Золотой", 25),
    PLATINUM("Платиновый", 30);

    private final String name;
    private final int percentsOfPurchase;

    public static PartnerLevel getByPurchaseBalance(double balance) {

        if (balance >= 0 && balance < 501) return STANDARD;
        if (balance >= 501 && balance < 1001) return SILVER;
        if (balance >= 1001 && balance < 2001) return GOLD;
        if (balance >= 2001) return PLATINUM;
        throw new RuntimeException("Partner level choose exception. Balance = " + balance);
    }
}
