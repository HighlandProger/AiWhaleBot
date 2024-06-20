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
    private static final int SCALE = 3;
    private static final String WEBHOOK = null;
    @JsonProperty("face_enhance")
    private static final boolean FACE_ENHANCE = false;

}
