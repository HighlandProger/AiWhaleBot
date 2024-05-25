package ru.rusguardian.constant.purchase;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.rusguardian.service.ai.constant.AIModel;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Getter
public enum SeparatePurchase {

    GPT4_50(AIModel.BalanceType.GPT_4, 50, 3.95),
    GPT4_100(AIModel.BalanceType.GPT_4, 100, 6.95),
    GPT4_200(AIModel.BalanceType.GPT_4, 200, 11.95),
    GPT4_500(AIModel.BalanceType.GPT_4, 500, 24.95),

    IMAGE_50(AIModel.BalanceType.IMAGE, 50, 3.95),
    IMAGE_100(AIModel.BalanceType.IMAGE, 100, 6.95),
    IMAGE_200(AIModel.BalanceType.IMAGE, 200, 11.95),
    IMAGE_500(AIModel.BalanceType.IMAGE, 500, 24.95),

    CLAUDE_1_MIL(AIModel.BalanceType.CLAUDE, 1000000, 5.96),
    CLAUDE_2_MIL(AIModel.BalanceType.CLAUDE, 2000000, 11.21),
    CLAUDE_3_MIL(AIModel.BalanceType.CLAUDE, 3000000, 15.71),
    CLAUDE_5_MIL(AIModel.BalanceType.CLAUDE, 5000000, 24.71),
    CLAUDE_10_MIL(AIModel.BalanceType.CLAUDE, 10000000, 46.46),
    CLAUDE_20_MIL(AIModel.BalanceType.CLAUDE, 20000000, 79.46),
    CLAUDE_50_MIL(AIModel.BalanceType.CLAUDE, 50000000, 181.95),

    SUNO_50(AIModel.BalanceType.MUSIC, 50, 3.20),
    SUNO_100(AIModel.BalanceType.MUSIC, 100, 6.30),
    SUNO_200(AIModel.BalanceType.MUSIC, 200, 9.50),
    SUNO_500(AIModel.BalanceType.MUSIC, 500, 19.70);

    private final AIModel.BalanceType balanceType;
    private final int count;
    private final double price;

    public static List<SeparatePurchase> getByBalanceType(AIModel.BalanceType balanceType) {
        return Arrays.stream(SeparatePurchase.values())
                .filter(purchase -> purchase.balanceType == balanceType)
                .toList();
    }
}
