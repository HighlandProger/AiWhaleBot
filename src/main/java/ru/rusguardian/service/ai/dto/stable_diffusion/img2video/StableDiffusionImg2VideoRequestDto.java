package ru.rusguardian.service.ai.dto.stable_diffusion.img2video;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StableDiffusionImg2VideoRequestDto {

    private String key;
    @JsonProperty("init_image")
    private String initImage;
    private String prompt;
    @JsonProperty("model_id")
    private String modelId;
    @JsonProperty("output_type")
    private String outputType = "mp4";

    public StableDiffusionImg2VideoRequestDto(String key, String initImage) {
        this.key = key;
        this.initImage = initImage;
    }
}
