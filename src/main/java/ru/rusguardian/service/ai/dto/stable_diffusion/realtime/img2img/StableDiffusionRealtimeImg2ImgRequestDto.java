package ru.rusguardian.service.ai.dto.stable_diffusion.realtime.img2img;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Deprecated
//UNKNOWN for use
public class StableDiffusionRealtimeImg2ImgRequestDto {

    private String key;
    @JsonProperty("init_image")
    private String initImage;
    private String prompt;
    @JsonProperty("negative_prompt")
    private String negativePrompt = "bad quality";
    private String samples = "4";
    @JsonProperty("enhance_prompt")
    private Boolean enhancePrompt = true;
    @JsonProperty("safety_checker")
    private Boolean safetyChecker = true;
    private Double strength = 0.7;

    public StableDiffusionRealtimeImg2ImgRequestDto(String key, String initImage, String prompt){
        this.key = key;
        this.initImage = initImage;
        this.prompt = prompt;
    }
}
