package ru.rusguardian.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;
import ru.rusguardian.constant.user.SubscriptionType;

@Entity
@Table(schema = "ncs_bot", name = "subscription_info")
@Getter
@ToString
public class SubscriptionInfo {

    @Id
    @Enumerated(EnumType.STRING)
    private SubscriptionType type;
    @Column
    private String smile;
    @Column
    private String name;
    @Column(name = "ru_name")
    private String ruName;
    @Column(name = "price")
    private float price;
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

}
