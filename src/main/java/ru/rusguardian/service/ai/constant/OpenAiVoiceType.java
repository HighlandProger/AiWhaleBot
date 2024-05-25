package ru.rusguardian.service.ai.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OpenAiVoiceType {

    ALLOY("alloy"),
    ECHO("echo"),
    FABLE("fable"),
    ONYX("onyx"),
    NOVA("nova"),
    SHIMMER("shimmer");

    private final String value;
}
