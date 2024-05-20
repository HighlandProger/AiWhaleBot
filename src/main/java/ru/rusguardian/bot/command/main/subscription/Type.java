package ru.rusguardian.bot.command.main.subscription;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Type {

    MONTH("Месячная", "мес"),
    YEAR("Годовая", "год");

    private final String ruFull;
    private final String ruShort;

}
