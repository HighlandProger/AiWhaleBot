package ru.rusguardian.service.ai.dto.stable_diffusion.text_to_image;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StableDiffusionModelTextToImageRequestDto {

    public StableDiffusionModelTextToImageRequestDto(String key, String prompt, SDModelId modelId) {
        this.key = key;
        this.prompt = prompt;
        this.modelId = modelId.getValue();
    }

    private String key;
    @JsonProperty("model_id")
    private String modelId;
    private String prompt;
    //TODO WARN OCCURS galucinations during 1024x1024
    private String width = "512";
    private String height = "512";
    private String samples = "4";
    @JsonProperty("num_inference_steps")
    private String numInferenceSteps = "21";
    @JsonProperty("safety_checker")
    private String safetyChecker = "no";
    @JsonProperty("enhance_prompt")
    private String enhancePrompt = "yes";
    @JsonProperty("guidance_scale")
    private double guidanceScale = 3;
    @JsonProperty("multi_lingual")
    private String multiLingual = "no";
    private String panorama = "no";
    @JsonProperty("self_attention")
    private String selfAttention = "no";
    private String upscale = "no";
    private String tomesd = "yes";
    @JsonProperty("clip_skip")
    private String clipSkip = "2";
    @JsonProperty("use_karras_sigmas")
    private String useKarrasSigmas = "yes";
}
