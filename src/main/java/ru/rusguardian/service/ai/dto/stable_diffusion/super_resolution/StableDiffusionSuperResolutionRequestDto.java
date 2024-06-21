package ru.rusguardian.service.ai.dto.stable_diffusion.super_resolution;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StableDiffusionSuperResolutionRequestDto {
    private String key;
    private String url;
    private int scale = 3;
    private String webhook = null;
    @JsonProperty("face_enhance")
    private boolean faceEnhance = false;

    public StableDiffusionSuperResolutionRequestDto(String key, String url) {
        this.key = key;
        this.url = url;
    }

}
