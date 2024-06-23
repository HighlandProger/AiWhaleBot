package ru.rusguardian.service.ai.dto.stable_diffusion.pix2pix;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StableDiffusionPix2PixRequestDto {

    private String key;
    @JsonProperty("init_image")
    private String initImage;
    private String prompt;
    @JsonProperty("image_guidance_scale")
    private int imageGuidanceScale = 1;
    private int steps = 50;
    @JsonProperty("guidance_scale")
    private int guidanceScale = 7;

    public StableDiffusionPix2PixRequestDto(String key, String initImage, String prompt) {
        this.key = key;
        this.initImage = initImage;
        this.prompt = prompt;
    }
}
