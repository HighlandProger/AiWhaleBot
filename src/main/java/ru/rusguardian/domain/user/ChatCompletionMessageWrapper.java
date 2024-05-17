package ru.rusguardian.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.ai.openai.api.OpenAiApi;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ChatCompletionMessageWrapper {

    @Column
    private String content;
    @Enumerated(EnumType.STRING)
    private OpenAiApi.ChatCompletionMessage.Role role;

    public ChatCompletionMessageWrapper(OpenAiApi.ChatCompletionMessage message) {
        this.content = message.content();
        this.role = message.role();
    }

    public OpenAiApi.ChatCompletionMessage getInner() {
        return new OpenAiApi.ChatCompletionMessage(content, role);
    }

    public static ChatCompletionMessageWrapper getWrapped(OpenAiApi.ChatCompletionMessage chatCompletionMessage) {
        return new ChatCompletionMessageWrapper(chatCompletionMessage.content(), chatCompletionMessage.role());
    }
}
