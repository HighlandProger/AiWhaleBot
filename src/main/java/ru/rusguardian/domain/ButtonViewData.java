package ru.rusguardian.domain;

import jakarta.persistence.*;
import lombok.*;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AILanguage;

@Entity
@Table(schema = "ncs_bot", name = "button_view_data")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ButtonViewData {

    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "command_name")
    private CommandName commandName;
    @Enumerated(EnumType.STRING)
    @Column(name = "ai_language")
    private AILanguage aiLanguage;
    @Column
    private String value;

}
