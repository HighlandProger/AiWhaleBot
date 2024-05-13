package ru.rusguardian.constant.ai;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AIUserModel {


    GPT_4_TURBO("gpt-4-turbo"),
    GPT_4("gpt-4"),
    GPT_3_5_TURBO_16k("gpt-3.5-turbo-16k"),
    GPT_3_5_TURBO("gpt-3.5-turbo"),
    CLAUDE_3_OPUS("claude-3-opus-20240229"),
    CLAUDE_3_SONNET("claude-3-sonnet-20240229"),
    CLAUDE_3_HAIKU("claude-3-haiku-20240307"),
    GEMINI_1_5_PRO("???");

    private final String modelName;
}
