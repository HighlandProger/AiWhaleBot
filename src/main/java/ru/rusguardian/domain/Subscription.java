package ru.rusguardian.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.service.ai.constant.AIModel;

@Entity
@Table(schema = "ncs_bot", name = "subscriptions")
@Getter
@ToString
public class Subscription {

    @Id
    @Enumerated(EnumType.STRING)
    private SubscriptionType type;
    @Column
    private String smile;
    @Column
    private String name;
    @Column(name = "ru_name")
    private String ruName;
    @Column(name = "one_month_price")
    private float oneMonthPrice;
    @Column(name = "gpt_3_day_limit")
    private int gpt3DayLimit;
    @Column(name = "gpt_4_day_limit")
    private int gpt4DayLimit;
    @Column(name = "image_day_limit")
    private int imageDayLimit;
    @Column(name = "song_month_limit")
    private int songMonthLimit;
    @Column(name = "claude_tokens_month_limit")
    private int claudeTokensMonthLimit;
    @Column(name = "claude_sale_size")
    private int claudeSaleSize;
    @Column(name = "context_and_tokens_x_size")
    private int contextAndTokensXSize;

    public int getAllowedBySubscriptionRequestCount(AIModel.BalanceType balanceType) {
        if (balanceType == AIModel.BalanceType.GPT_3) {
            return this.getGpt3DayLimit();
        }
        if (balanceType == AIModel.BalanceType.GPT_4) {
            return this.getGpt4DayLimit();
        }
        if (balanceType == AIModel.BalanceType.IMAGE) {
            return this.getImageDayLimit();
        }
        if (balanceType == AIModel.BalanceType.CLAUDE) {
            return this.getClaudeTokensMonthLimit();
        }
        if (balanceType == AIModel.BalanceType.MUSIC) {
            return this.getSongMonthLimit();
        }
        throw new RuntimeException("UNKNOWN BALANCE TYPE: " + balanceType);
    }
}
