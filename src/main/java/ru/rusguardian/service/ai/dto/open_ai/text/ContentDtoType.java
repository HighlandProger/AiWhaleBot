package ru.rusguardian.service.ai.dto.open_ai.text;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ContentDtoType {
    TEXT("text"),
    IMAGE_URL("image_url");

    private final String value;
}
