package ru.rusguardian.service.process.prompt;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.AITextService;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.ai.constant.Role;
import ru.rusguardian.service.ai.dto.open_ai.text.ContentDto;
import ru.rusguardian.service.ai.dto.open_ai.text.ImageUrlDto;
import ru.rusguardian.service.ai.dto.open_ai.text.OpenAiTextRequestDto;
import ru.rusguardian.service.ai.dto.open_ai.text.RequestMessageDto;
import ru.rusguardian.service.data.ChatCompletionMessageService;
import ru.rusguardian.service.process.transactional.ProcessTransactionalAITextRequestUpdate;
import ru.rusguardian.util.ChatUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class ProcessPromptVision {

    private final ProcessTransactionalAITextRequestUpdate transactionalAITextRequestUpdate;
    private final AITextService aiTextService;
    private final ChatCompletionMessageService chatCompletionMessageService;

    @Async
    public CompletableFuture<String> process(Chat chat, String imageUrl, String prompt) {

        OpenAiTextRequestDto requestDto = getRequestDto(chat, imageUrl, prompt);
        return aiTextService.getText(requestDto)
                .thenApply(responseDto -> {
                    transactionalAITextRequestUpdate.update(chat, prompt, responseDto);
                    return responseDto.getChoices().get(0).getMessage().getContent();
                });
    }

    private OpenAiTextRequestDto getRequestDto(Chat chat, String imageUrl, String prompt) {
        OpenAiTextRequestDto dto = new OpenAiTextRequestDto();
        dto.setModel(AIModel.GPT_4_OMNI.getModelName());
        dto.setMaxTokens(ChatUtil.getChatMaxTokens(chat));
        dto.setTemperature(chat.getAiSettingsEmbedded().getTemperature());
        dto.setMessages(getChatMessages(chat, imageUrl, prompt));
        dto.setUser((String.valueOf(chat.getId())));

        return dto;
    }

    private List<RequestMessageDto> getChatMessages(Chat chat, String imageUrl, String prompt) {
        List<RequestMessageDto> chatMessages = new ArrayList<>();
        chatMessages.addAll(ChatUtil.getLeadingChatCompletionMessages(chat, chatCompletionMessageService).stream().map(RequestMessageDto::new).toList());
        chatMessages.add((getImageRequestMessageDto(imageUrl, prompt)));

        return chatMessages;
    }

    private RequestMessageDto getImageRequestMessageDto(String imageUrl, String prompt) {
        RequestMessageDto dto = new RequestMessageDto();
        dto.setContent(List.of(new ContentDto(new ImageUrlDto(imageUrl)), new ContentDto(prompt)));
        dto.setRole(Role.USER.getValue());

        return dto;
    }
}
