package ru.rusguardian.domain;

import jakarta.persistence.*;
import lombok.*;
import ru.rusguardian.domain.user.Chat;

import java.time.LocalDateTime;

@Entity
@Table(schema = "ncs_bot", name = "log_events")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LogEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;
    @Column(name = "event_time")
    private LocalDateTime eventTime;
    @Column
    private String event;

    public LogEvent(Chat chat, LocalDateTime eventTime, String event) {
        this.chat = chat;
        this.eventTime = eventTime;
        this.event = event;
    }
}
