package ru.rusguardian.constant.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.rusguardian.bot.command.main.subscription.Type;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Getter
public enum SubscriptionType {

    FREE(null),
    MINI_MONTH(Type.MONTH),
    MINI_YEAR(Type.YEAR),
    STARTER_MONTH(Type.MONTH),
    STARTER_YEAR(Type.YEAR),
    PREMIUM_MONTH(Type.MONTH),
    PREMIUM_YEAR(Type.YEAR),
    ULTIMATE_MONTH(Type.MONTH),
    ULTIMATE_YEAR(Type.YEAR),
    ALPHA_MONTH(Type.MONTH),
    ALPHA_YEAR(Type.YEAR);

    private final Type timeType;

    public List<SubscriptionType> getByTimeType(Type timeType) {
        return Arrays.stream(SubscriptionType.values()).filter(e -> e.timeType == timeType).toList();
    }
}
