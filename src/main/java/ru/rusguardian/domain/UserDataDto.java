package ru.rusguardian.domain;

import lombok.Data;
import ru.rusguardian.service.ai.dto.stable_diffusion.img2video.VideoModel;

@Data
public class UserDataDto {

    private Long id;
    private String prompt;
    private String imageUrl;
    private VideoModel videoModel;
}
