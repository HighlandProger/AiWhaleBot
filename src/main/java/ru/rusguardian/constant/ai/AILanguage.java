package ru.rusguardian.constant.ai;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AILanguage {

    RUSSIAN("ru", "\uD83C\uDDF7\uD83C\uDDFA", "Русский"),
    ENGLISH("en", "\uD83C\uDDFA\uD83C\uDDF8", "English"),
    GERMAN("de", "\uD83C\uDDE9\uD83C\uDDEA", "Deutsch"),
    UZBEK("uz", "\uD83C\uDDFA\uD83C\uDDFF", "O'zbek tili");

    private final String value;
    private final String smile;
    private final String description;

}
