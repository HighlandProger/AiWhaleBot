package ru.rusguardian.domain.user;

import jakarta.persistence.*;
import lombok.Data;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.converters.CommandNameConverter;

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
    @Convert(converter = CommandNameConverter.class)
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

    @Embedded
    private SubscriptionEmbedded subscriptionEmbedded;
    @Embedded
    private AISettingsEmbedded aiSettingsEmbedded;
    @Embedded
    private UserBalanceEmbedded userBalanceEmbedded;
    @Embedded
    private PartnerEmbedded partnerEmbeddedInfo;

}
