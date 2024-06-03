package ru.rusguardian.domain;

import jakarta.persistence.*;
import lombok.*;
import ru.rusguardian.bot.command.service.CommandName;

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
    @Column(name = "button_number")
    private int buttonNumber;
    @Column(name = "ru_value")
    private String ruValue;
    @Column(name = "en_value")
    private String enValue;
    @Column(name = "de_value")
    private String deValue;
    @Column(name = "uz_value")
    private String uzValue;
}
