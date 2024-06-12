package ru.rusguardian.service.ai.dto.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.ai.dto.anthropic.AnthropicTextResponseDto;
import ru.rusguardian.service.ai.dto.open_ai.text.OpenAiTextResponseDto;

@Service
@Slf4j
public class AiResponseCommonDtoFactory {

    public AiResponseCommonDto create(OpenAiTextResponseDto dto){
        AiResponseCommonDto responseDto = new AiResponseCommonDto();
        //TODO add notification for owner
        if(dto.getChoices().size()>1) {log.warn("OPEN AI SIZE CHOICES > 1");}

        responseDto.setAiResponse(dto.getChoices().get(0).getMessage().getContent());
        responseDto.setModel(AIModel.getByModelName(dto.getModel()));
        responseDto.setPromptTokens(dto.getUsage().getPromptTokens());
        responseDto.setCompletionTokens(dto.getUsage().getCompletionTokens());
        responseDto.setTotalTokens(dto.getUsage().getTotalTokens());
        return responseDto;
    }

    public AiResponseCommonDto create(AnthropicTextResponseDto dto){
        AiResponseCommonDto responseDto = new AiResponseCommonDto();
        //TODO add notification for owner
        if(dto.getContent().size()>1) {log.warn("ANTHROPIC SIZE CONTENT > 1");}

        responseDto.setAiResponse(dto.getContent().get(0).getText());
        responseDto.setModel(AIModel.getByModelName(dto.getModel()));
        responseDto.setPromptTokens(dto.getUsage().getInputTokens());
        responseDto.setCompletionTokens(dto.getUsage().getOutputTokens());
        responseDto.setTotalTokens(dto.getUsage().getInputTokens() + dto.getUsage().getOutputTokens());
        return responseDto;
    }
}
