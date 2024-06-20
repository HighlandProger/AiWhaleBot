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
public class StableDiffusionTextToImageRequestDto {

    public StableDiffusionTextToImageRequestDto(String prompt) {
        this.prompt = prompt;
    }

    private String key;
    private String prompt;
    /**
     * Items you don't want in the text_to_image.
     */
    @JsonProperty("negative_prompt")
    private String negativePrompt;
    /**
     * Max Height: Width: 1024x1024.
     */
    private String width;
    /**
     * Max Height: Width: 1024x1024.
     */
    private String height;
    /**
     * Number of images to be returned in response. The maximum value is 4.
     */
    private int samples;
    /**
     * A checker for NSFW images. If such an text_to_image is detected, it will be replaced by a blank text_to_image.
     */
    @JsonProperty("safety_checker")
    private boolean safetyChecker;
    /**
     * Seed is used to reproduce results, same seed will give you same text_to_image in return again. Pass null for a random number.
     */
    private String seed;
    /**
     * Seed is used to reproduce results, same seed will give you same text_to_image in return again. Pass null for a random number.
     */
    @JsonProperty("instant_response")
    private boolean instantResponse;
    /**
     * Get response as base64 string, default: false, options: true or false
     */
    private boolean base64;
    /**
     * Set an URL to get a POST API call once the text_to_image generation is complete.
     */
    private String webhook;
    /**
     * This ID is returned in the response to the webhook API call. This will be used to identify the webhook request.
     */
    @JsonProperty("track_id")
    private String trackId;
    /**
     * Enhance prompts for better results; options: true/false
     */
    @JsonProperty("enhance_prompt")
    private boolean enhancePrompt;
    /**
     * This allows you to set the style of the text_to_image for better result. Check Stable diffusion api
     */
    @JsonProperty("enhance_style")
    private String enhanceStyle;

}
