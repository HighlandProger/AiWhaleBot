package ru.rusguardian.service.ai.dto.anthropic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.rusguardian.domain.ChatCompletionMessage;
import ru.rusguardian.service.ai.constant.Role;
import ru.rusguardian.service.ai.dto.anthropic.constant.ContentType;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnthropicTextRequestDto {
    private String model;
    private List<Message> messages;
    @JsonProperty("max_tokens")
    private Integer maxTokens;
    private String system;
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    private double temperature;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Message{
        private String role;
        private List<Content> content;

        public Message(Role role, String text){
            this.role = role.getValue();
            this.content = List.of(new Content(text));
        }

        public Message(ChatCompletionMessage message){
            this.role = message.getRole().getValue();
            this.content = List.of(new Content(message.getMessage()));
        }

        @AllArgsConstructor
        @NoArgsConstructor
        @Data
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Content{
            private String type;
            private String text;
            private Source source;

            public Content(String text){
                this.type = ContentType.TEXT.getValue();
                this.text = text;
            }

            public static class Source{
                private final String type = "base64";
                @JsonProperty("media_type")
                private String mediaType;
                private String data;
            }
        }
    }
}
