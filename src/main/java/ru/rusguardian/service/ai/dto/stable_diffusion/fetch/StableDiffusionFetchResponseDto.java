package ru.rusguardian.service.ai.dto.stable_diffusion.fetch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.rusguardian.service.ai.dto.stable_diffusion.StringToEmptyListDeserializer;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StableDiffusionFetchResponseDto {

    private String status;
    private Long id;
    private String message;
    @JsonDeserialize(using = StringToEmptyListDeserializer.class)
    private List<String> output;
    @JsonProperty("proxy_links")
    private String proxyLinks;

}
