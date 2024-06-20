package ru.rusguardian.service.ai.dto.stable_diffusion;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class StableDiffusionV6Pix2PixResponseDto {

    private String status;
    private String tip;
    private Double eta;
    private String message;
    @JsonProperty("fetch_result")
    private String fetchResult;
    private Long id;
    @JsonDeserialize(using = StringToEmptyListDeserializer.class)
    private List<String> output;
    private MetaDto meta;

    //ERROR MESSAGE HERE
    private Messege messege;

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    //ERROR MESSAGE HERE
    public static class Messege {
        @JsonDeserialize(using = StringToEmptyListDeserializer.class)
        private List<String> image;
    }
}
