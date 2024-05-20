package ru.rusguardian.constant.purchase;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.rusguardian.service.ai.constant.AIModel;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Getter
public enum SeparatePurchase {

    GPT4_50(AIModel.BalanceType.GPT_4, 50, 3.95f),
    GPT4_100(AIModel.BalanceType.GPT_4, 100, 6.95f),
    GPT4_200(AIModel.BalanceType.GPT_4, 200, 11.95f),
    GPT4_500(AIModel.BalanceType.GPT_4, 500, 24.95f),

    IMAGE_50(AIModel.BalanceType.IMAGE, 50, 3.95f),
    IMAGE_100(AIModel.BalanceType.IMAGE, 100, 6.95f),
    IMAGE_200(AIModel.BalanceType.IMAGE, 200, 11.95f),
    IMAGE_500(AIModel.BalanceType.IMAGE, 500, 24.95f),

    CLAUDE_1_MIL(AIModel.BalanceType.CLAUDE, 1000000, 5.96f),
    CLAUDE_2_MIL(AIModel.BalanceType.CLAUDE, 2000000, 11.21f),
    CLAUDE_3_MIL(AIModel.BalanceType.CLAUDE, 3000000, 15.71f),
    CLAUDE_5_MIL(AIModel.BalanceType.CLAUDE, 5000000, 24.71f),
    CLAUDE_10_MIL(AIModel.BalanceType.CLAUDE, 10000000, 46.46f),
    CLAUDE_20_MIL(AIModel.BalanceType.CLAUDE, 20000000, 79.46f),
    CLAUDE_50_MIL(AIModel.BalanceType.CLAUDE, 50000000, 181.95f),

    SUNO_50(AIModel.BalanceType.MUSIC, 50, 3.20f),
    SUNO_100(AIModel.BalanceType.MUSIC, 100, 6.30f),
    SUNO_200(AIModel.BalanceType.MUSIC, 200, 9.50f),
    SUNO_500(AIModel.BalanceType.MUSIC, 500, 19.70f);

    private final AIModel.BalanceType balanceType;
    private final int count;
    private final float price;

    public static List<SeparatePurchase> getByBalanceType(AIModel.BalanceType balanceType) {
        return Arrays.stream(SeparatePurchase.values())
                .filter(purchase -> purchase.balanceType == balanceType)
                .toList();
    }
}
