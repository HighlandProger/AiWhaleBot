package ru.rusguardian.constant.ai;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static ru.rusguardian.constant.ai.Provider.ANTHROPIC;
import static ru.rusguardian.constant.ai.Provider.OPEN_AI;

@RequiredArgsConstructor
@Getter
public enum AIModel {


    GPT_4_TURBO(OPEN_AI, "gpt-4-turbo"),
    GPT_4(OPEN_AI, "gpt-4"),
    GPT_3_5_TURBO_16k(OPEN_AI, "gpt-3.5-turbo-16k"),
    GPT_3_5_TURBO(OPEN_AI, "gpt-3.5-turbo"),
    ADA_V2_EMBEDDINGS(OPEN_AI, "ada-v2-embedding"),
    DALL_E_2(OPEN_AI, "dall-e-2"),
    DALL_E_3(OPEN_AI, "dall-e-3"),
    WHISPER(OPEN_AI, "whisper"),
    TTS(OPEN_AI, "tts"),
    CLAUDE_3_OPUS(ANTHROPIC, "claude-3-opus-20240229"),
    CLAUDE_3_SONNET(ANTHROPIC, "claude-3-sonnet-20240229"),
    CLAUDE_3_HAIKU(ANTHROPIC, "claude-3-haiku-20240307");


    private final Provider provider;
    private final String modelName;
}
