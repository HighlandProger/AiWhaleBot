package ru.rusguardian.service.ai.dto.anthropic;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AnthropicTextResponseDto {

    private String id;
    private String type;
    private String role;
    private String model;
    private List<Content> content;
    @JsonProperty("stop_reason")
    private String stopReason;
    @JsonProperty("stop_sequence")
    private String stopSequence;
    private Usage usage;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Content{
        private String type;
        private String text;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Usage{
        @JsonProperty("input_tokens")
        private int inputTokens;
        @JsonProperty("output_tokens")
        private int outputTokens;
    }
}
