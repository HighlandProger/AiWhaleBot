package ru.rusguardian.service.ai.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AIRequestSetting {
    TOKEN_SIZE(512),
    MESSAGES_IN_LIST(5);

    private final int value;
}
