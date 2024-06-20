package ru.rusguardian.service.ai.dto.stable_diffusion.remove_background;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StableDiffusionRemoveBackgroundRequestDto {
    private String key;
    private String image;
}
