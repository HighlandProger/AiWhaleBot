package ru.rusguardian.service.ai.dto.open_ai.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OpenAiTextToImageResponseDto {

    private long created;
    private List<Data> data;

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private String url;
        @JsonProperty("b64_json")
        private String base64Json;
    }
}
