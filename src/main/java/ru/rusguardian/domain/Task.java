package ru.rusguardian.domain;

import jakarta.persistence.*;
import lombok.*;
import ru.rusguardian.bot.command.service.CommandName;

import java.time.LocalDateTime;

@Entity
@Table(schema = "ncs_bot", name = "tasks")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "chat_id")
    private Long chatId;
    @Enumerated(EnumType.STRING)
    @Column(name = "command_to_execute")
    private CommandName commandToExecute;
    @Column(name = "execute_time")
    private LocalDateTime executeTime;
    @Column(name = "command_to_check")
    private CommandName commandToCheck;

    public Task(Long chatId, CommandName commandToExecute, LocalDateTime executeTime, CommandName commandToCheck) {
        this.chatId = chatId;
        this.commandToExecute = commandToExecute;
        this.executeTime = executeTime;
        this.commandToCheck = commandToCheck;
    }
}
