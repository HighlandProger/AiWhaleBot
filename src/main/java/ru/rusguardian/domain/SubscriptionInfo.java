package ru.rusguardian.domain;

import jakarta.persistence.*;
import lombok.Data;
import ru.rusguardian.constant.user.SubscriptionType;

@Entity
@Table(schema = "ncs_bot", name = "subscription_info")
@Data
public class SubscriptionInfo {

    @Id
    @Enumerated(EnumType.STRING)
    private SubscriptionType type;
    @Column
    private String smile;
    @Column(name = "ru_name")
    private String ruName;
    @Column(name = "month_cost")
    private float monthCost;
    @Column(name = "year_cost")
    private float yearCost;
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

}
