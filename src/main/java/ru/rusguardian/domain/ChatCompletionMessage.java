package ru.rusguardian.domain;

import jakarta.persistence.*;
import lombok.*;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.Role;

@Entity
@Table(schema = "ncs_bot", name = "chat_completion_messages")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatCompletionMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String message;
}
