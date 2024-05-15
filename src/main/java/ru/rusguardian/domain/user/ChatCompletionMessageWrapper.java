package ru.rusguardian.domain.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.api.OpenAiApi;

@Entity
@Table(schema = "ncs_bot", name = "chat_completion_messages")
@Data
@RequiredArgsConstructor
public class ChatCompletionMessageWrapper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;
    @Column
    private final String content;
    @Enumerated(EnumType.STRING)
    private final OpenAiApi.ChatCompletionMessage.Role role;

    public ChatCompletionMessageWrapper(OpenAiApi.ChatCompletionMessage message) {
        this.content = message.content();
        this.role = message.role();
    }

    public OpenAiApi.ChatCompletionMessage getInner() {
        return new OpenAiApi.ChatCompletionMessage(content, role);
    }
}
