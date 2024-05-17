package ru.rusguardian.constant.ai;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AILanguage {

    RUSSIAN("ru", "\uD83C\uDDF7\uD83C\uDDFA"),
    ENGLISH("en", "\uD83C\uDDFA\uD83C\uDDF8"),
    GERMAN("ge", "\uD83C\uDDE9\uD83C\uDDEA");

    private final String value;
    private final String smile;

}
