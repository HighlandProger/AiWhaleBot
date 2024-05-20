package ru.rusguardian.constant.purchase;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PurchaseProvider {

    ROBOKASSA("Robokassa"),
    PAYSELECTION("Payselection"),
    CRYPTOCLOUD("Crypto");

    private final String name;
}
