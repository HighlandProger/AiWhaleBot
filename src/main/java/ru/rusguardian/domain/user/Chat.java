package ru.rusguardian.domain.user;

import jakarta.persistence.*;
import lombok.Data;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.SubscriptionInfo;
import ru.rusguardian.service.ai.constant.AIModel;

import java.time.LocalDateTime;

@Entity
@Table(schema = "ncs_bot", name = "chats")
@Data
public class Chat {

    @Id
    @Column
    private long id;
    @Column(name = "username")
    private String username;
    @Column(name = "telegram_first_name")
    private String telegramFirstName;
    @Column(name = "telegram_last_name")
    private String telegramLastName;
    @Enumerated(EnumType.STRING)
    @Column(name = "next_command")
    private CommandName nextCommand;
    @Column(name = "registration_time")
    private LocalDateTime registrationTime;
    @Column(name = "is_admin")
    private boolean isAdmin;
    @Column(name = "start_info")
    private String startInfo;
    @Column(name = "traffic_link")
    private String trafficLink;
    @Column(name = "campaign")
    private String campaign;
    @Column(name = "is_banned")
    private boolean isBanned;

    @Embedded
    private SubscriptionEmbedded subscriptionEmbedded;
    @Embedded
    private AISettingsEmbedded aiSettingsEmbedded;
    @Embedded
    private UserBalanceEmbedded userBalanceEmbedded;
    @Embedded
    private PartnerEmbedded partnerEmbeddedInfo;

    public int getAllowedBySubscriptionRequestCount(AIModel.BalanceType balanceType) {
        SubscriptionInfo subscriptionInfo = this.getSubscriptionEmbedded().getSubscriptionInfo();
        if (balanceType == AIModel.BalanceType.GPT_3) {
            return subscriptionInfo.getGpt3DayLimit();
        }
        if (balanceType == AIModel.BalanceType.GPT_4) {
            return subscriptionInfo.getGpt4DayLimit();
        }
        if (balanceType == AIModel.BalanceType.IMAGE) {
            return subscriptionInfo.getImageDayLimit();
        }
        if (balanceType == AIModel.BalanceType.CLAUDE) {
            return subscriptionInfo.getClaudeTokensMonthLimit();
        }
        if (balanceType == AIModel.BalanceType.MUSIC) {
            return subscriptionInfo.getSongMonthLimit();
        }
        throw new RuntimeException("UNKNOWN BALANCE TYPE: " + balanceType);
    }
}
