package ru.rusguardian.service.ai.dto.stable_diffusion.text_to_image.realtime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StableDiffusionRealtimeTextToImageRequestDto {

    public StableDiffusionRealtimeTextToImageRequestDto(String key, String prompt) {
        this.key = key;
        this.prompt = prompt;
    }

    private String key;
    private String prompt;
    /**
     * Items you don't want in the text_to_image.
     */
    @JsonProperty("negative_prompt")
    private String negativePrompt = "Worst quality, Normal quality, Low quality, Low res, Blurry, Jpeg artifacts, Grainy, text, logo, watermark, banner, extra digits, signature, Cropped, Out of frame, Out of focus,extra fingers, mutated hands, poorly drawn hands, poorly drawn face, deformed, ugly, blurry, bad anatomy, bad proportions, extra limbs, cloned face, skinny, glitchy, double torso, mangled fingers, missing lips, ugly face, distorted face, extra legs, fuzzy eyes, mutated eyes, disproportionate pupils, blurry eyes, Bad anatomy, Bad proportions, Deformed, Disconnected limbs, Disfigured, Extra arms, Extra limbs, Extra hands, Fused fingers, Gross proportions, Long neck, Malformed limbs, Mutated, Mutated hands, Mutated limbs, Missing arms, Missing fingers, Poorly drawn hands, Poorly drawn face, fuzzy eyes";
    //TODO WARN OCCURS galucinations during 1024x1024
    /**
     * Max Height: Width: 1024x1024.
     */
    private String width = "1024";
    /**
     * Max Height: Width: 1024x1024.
     */
    private String height = "1024";
    /**
     * Number of images to be returned in response. The maximum value is 4.
     */
    private Integer samples = 4;
    /**
     * A checker for NSFW images. If such an text_to_image is detected, it will be replaced by a blank text_to_image.
     */
    @JsonProperty("safety_checker")
    private Boolean safetyChecker = true;
    /**
     * Get response as base64 string, default: false, options: true or false
     */
    private Boolean base64 = false;
    /**
     * Set an URL to get a POST API call once the text_to_image generation is complete.
     */
    private String webhook;
    /**
     * This ID is returned in the response to the webhook API call. This will be used to identify the webhook request.
     */
    @JsonProperty("track_id")
    private String trackId;

    @JsonProperty("guidance_scale")
    private Integer guidanceScale = 8;

}
