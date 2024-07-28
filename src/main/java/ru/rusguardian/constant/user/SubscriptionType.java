package ru.rusguardian.constant.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.rusguardian.bot.command.main.subscription.Type;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Getter
public enum SubscriptionType {

    FREE,
    MINI,
    STARTER,
    PREMIUM,
    ULTIMATE,
    ALPHA;


}
