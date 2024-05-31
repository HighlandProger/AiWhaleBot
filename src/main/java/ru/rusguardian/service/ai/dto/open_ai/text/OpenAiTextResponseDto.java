package ru.rusguardian.service.ai.dto.open_ai.text;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.rusguardian.domain.ChatCompletionMessage;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OpenAiTextResponseDto {

    private String id;
    private String object;
    private long created;
    private String model;
    @JsonProperty("system_fingerprint")
    private String systemFingerprint;
    private List<Choice> choices;
    private Usage usage;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {

        private int index;
        private ResponseMessageDto message;
        @JsonProperty("finish_reason")
        private String finishReason;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ResponseMessageDto {
            private String content;
            private String role;

            public ResponseMessageDto(ChatCompletionMessage message) {
                this.content = message.getMessage();
                this.role = message.getRole().getValue();
            }
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {
        @JsonProperty("prompt_tokens")
        private int promptTokens;
        @JsonProperty("completion_tokens")
        private int completionTokens;
        @JsonProperty("total_tokens")
        private int totalTokens;
    }

}
