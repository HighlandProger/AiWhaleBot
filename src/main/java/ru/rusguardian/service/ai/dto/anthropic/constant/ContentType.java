package ru.rusguardian.service.ai.dto.anthropic.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ContentType {

    TEXT("text"),
    IMAGE("image");

    private final String value;
}
