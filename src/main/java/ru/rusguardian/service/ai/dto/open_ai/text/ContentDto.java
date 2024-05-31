package ru.rusguardian.service.ai.dto.open_ai.text;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContentDto {
    private String type;
    private String text;
    @JsonProperty("image_url")
    private ImageUrlDto imageUrl;

    public ContentDto(String textPrompt) {
        this.text = textPrompt;
        this.type = ContentDtoType.TEXT.getValue();
    }

    public ContentDto(ImageUrlDto imageUrlDto) {
        this.type = ContentDtoType.IMAGE_URL.getValue();
        this.imageUrl = imageUrlDto;
    }

}
