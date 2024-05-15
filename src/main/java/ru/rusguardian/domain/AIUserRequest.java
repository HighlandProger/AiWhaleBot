package ru.rusguardian.domain;

import jakarta.persistence.*;
import lombok.*;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.AIModel;

import java.time.LocalDateTime;

@Entity
@Table(schema = "ncs_bot", name = "ai_user_requests")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AIUserRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;
    @Column(name = "request_time")
    private LocalDateTime requestTime;
    @Enumerated(EnumType.STRING)
    @Column(name = "ai_model")
    private AIModel aiModel;
    @Column(name = "prompt_tokens")
    private int promptTokens;
    @Column(name = "completion_tokens")
    private int completionTokens;
    @Column
    private int totalTokens;


}
