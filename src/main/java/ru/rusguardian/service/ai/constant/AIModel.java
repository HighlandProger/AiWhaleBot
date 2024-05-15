package ru.rusguardian.service.ai.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.rusguardian.service.ai.constant.Provider.*;

@RequiredArgsConstructor
@Getter
public enum AIModel {

    GPT_4_OMNI(OPEN_AI, "gpt-4-o"),
    GPT_4_TURBO(OPEN_AI, "gpt-4-turbo"),
    GPT_4(OPEN_AI, "gpt-4"),
    GPT_3_5_TURBO_16_K(OPEN_AI, "gpt-3.5-turbo-16k"),
    GPT_3_5_TURBO(OPEN_AI, "gpt-3.5-turbo"),
    ADA_V2_EMBEDDINGS(OPEN_AI, "ada-v2-embedding"),
    DALL_E_2(OPEN_AI, "dall-e-2"),
    DALL_E_3(OPEN_AI, "dall-e-3"),
    WHISPER(OPEN_AI, "whisper"),
    TTS(OPEN_AI, "tts"),
    CLAUDE_3_OPUS(ANTHROPIC, "claude-3-opus-20240229"),
    CLAUDE_3_SONNET(ANTHROPIC, "claude-3-sonnet-20240229"),
    CLAUDE_3_HAIKU(ANTHROPIC, "claude-3-haiku-20240307"),
    GEMINI_1_5_PRO(GOOGLE, "???");

    private final Provider provider;
    private final String modelName;

    private static final List<AIModel> getUserChatModels = List.of(
            AIModel.GPT_4_OMNI,
            AIModel.GPT_4_TURBO,
            AIModel.GPT_4,
            AIModel.GPT_3_5_TURBO_16_K,
            AIModel.GPT_3_5_TURBO,
            AIModel.CLAUDE_3_OPUS,
            AIModel.CLAUDE_3_SONNET,
            AIModel.CLAUDE_3_HAIKU,
            AIModel.GEMINI_1_5_PRO
    );

    private static final Map<String, AIModel> MODEL_NAME_MAP = new HashMap<>();

    static {
        for (AIModel m : AIModel.values()) {
            if (m.getModelName() != null) MODEL_NAME_MAP.put(m.getModelName(), m);
        }
    }

    public static AIModel getByModelName(String modelName) {
        return MODEL_NAME_MAP.get(modelName);
    }

}
