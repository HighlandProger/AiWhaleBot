package ru.rusguardian.service.ai.dto.stable_diffusion.img2video;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum VideoModel {
    SVD("svd"),
    DARK_SUSHI_MIX_VID("dark-sushi-mix-vid"),
    EPICREALISMNATURALSI_VID("epicrealismnaturalsi-vid"),
    HELLONIJICUTE25D_VID("hellonijicute25d-vid");

    private final String value;
}
