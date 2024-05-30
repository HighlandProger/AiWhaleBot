package ru.rusguardian.service.ai.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.rusguardian.service.ai.constant.Provider.*;

@RequiredArgsConstructor
@Getter
@Slf4j
public enum AIModel {

    GPT_4_OMNI(OPEN_AI, BalanceType.GPT_4, "gpt-4o-2024-05-13"),
    GPT_4_TURBO(OPEN_AI, BalanceType.GPT_4, "gpt-4-turbo"),
    GPT_4(OPEN_AI, BalanceType.GPT_4, "gpt-4-0613"),
    GPT_3_5_TURBO_16_K(OPEN_AI, BalanceType.GPT_3, "gpt-3.5-turbo-16k"),
    GPT_3_5_TURBO(OPEN_AI, BalanceType.GPT_3, "gpt-3.5-turbo-0125"),
    ADA_V2_EMBEDDINGS(OPEN_AI, null, "ada-v2-embedding"),
    DALL_E_2(OPEN_AI, BalanceType.IMAGE, "dall-e-2"),
    DALL_E_3(OPEN_AI, BalanceType.IMAGE, "dall-e-3"),
    STABLE_DIFFUSION(Provider.STABLE_DIFFUSION, BalanceType.IMAGE, "Stable Diffusion"),
    MIDJOURNEY(Provider.MIDJOURNEY, BalanceType.IMAGE, "Midjourney"),
    WHISPER(OPEN_AI, null, "whisper-1"),
    TTS(OPEN_AI, null, "tts-1"),
    CLAUDE_3_OPUS(ANTHROPIC, BalanceType.CLAUDE, "claude-3-opus-20240229"),
    CLAUDE_3_SONNET(ANTHROPIC, BalanceType.CLAUDE, "claude-3-sonnet-20240229"),
    CLAUDE_3_HAIKU(ANTHROPIC, BalanceType.CLAUDE, "claude-3-haiku-20240307"),
    GEMINI_1_5_PRO(GOOGLE, BalanceType.GPT_3, "???");

    private final Provider provider;
    private final BalanceType balanceType;
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
        AIModel model = MODEL_NAME_MAP.get(modelName);
        if (model == null) {
            log.error("UNKNOWN model type {}", modelName);
            throw new RuntimeException("AIModel parsing exception");
        }
        return model;
    }

    public static List<AIModel> getByBalanceType(BalanceType balanceType) {
        return Arrays.stream(AIModel.values())
                .filter(model -> model.balanceType == balanceType)
                .toList();
    }

    public enum BalanceType {
        GPT_3,
        GPT_4,
        IMAGE,
        MUSIC,
        CLAUDE
    }
}
