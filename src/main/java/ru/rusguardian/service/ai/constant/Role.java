package ru.rusguardian.service.ai.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum Role {

    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant");

    public static Role getByValue(String value){
        return Arrays.stream(Role.values()).filter(e -> e.getValue().equals(value)).findFirst().orElseThrow();
    }

    private final String value;
}
