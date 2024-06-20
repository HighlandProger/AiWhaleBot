package ru.rusguardian.service.ai.dto.stable_diffusion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MetaDto {
    private String base64;
    @JsonProperty("enhance_prompt")
    private String enhancePrompt;
    @JsonProperty("file_prefix")
    private String filePrefix;
    @JsonProperty("guidance_scale")
    private Long guidanceScale;
    private Integer height;
    private Integer width;
    @JsonProperty("instant_response")
    private String instantResponse;
    @JsonProperty("n_samples")
    private Integer nSamples;
    @JsonProperty("negative_prompt")
    private String negativePrompt;
    private String outdir;
    private String prompt;
    @JsonProperty("safety_checker")
    private String safetyChecker;
    @JsonProperty("safety_checker_type")
    private String safetyCheckerType;
    private Long seed;
    private String temp;

}
