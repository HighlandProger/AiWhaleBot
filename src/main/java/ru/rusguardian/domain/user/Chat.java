package ru.rusguardian.domain.user;

import jakarta.persistence.*;
import lombok.*;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AIUserModel;
import ru.rusguardian.constant.ai.AssistantRole;
import ru.rusguardian.domain.converters.AIUserModelConverter;
import ru.rusguardian.domain.converters.AssistantRoleConverter;
import ru.rusguardian.domain.converters.CommandNameConverter;

import java.time.LocalDateTime;

@Entity
@Table(schema = "ncs_bot", name = "chats")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Chat {

    public Chat(String username, boolean isAdmin) {
        this.username = username;
        this.isAdmin = isAdmin;
    }

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
    @Convert(converter = AssistantRoleConverter.class)
    @Column(name = "assistant_role")
    private AssistantRole assistantRole;
    @OneToOne
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;
    @Column(name = "ai_language")
    private String aiLanguage;
    private float temperature;
    @Convert(converter = AIUserModelConverter.class)
    @Column(name = "ai_user_model")
    private AIUserModel aiUserModel;


}
