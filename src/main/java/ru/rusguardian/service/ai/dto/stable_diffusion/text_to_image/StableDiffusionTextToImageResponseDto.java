package ru.rusguardian.service.ai.dto.stable_diffusion.text_to_image;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.rusguardian.service.ai.dto.stable_diffusion.MetaDto;

import java.util.List;

@NoArgsConstructor
@Data
public class StableDiffusionTextToImageResponseDto {

    private String status;
    private String message;
    private Double generationTime;
    private Long id;
    private List<String> output;
    @JsonProperty("proxy_links")
    private List<String> proxyLinks;
    private MetaDto meta;

}
