package ru.rusguardian.bot.command.prompts.process;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.rusguardian.constant.ai.AssistantRole;
import ru.rusguardian.domain.AIUserRequest;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.domain.user.ChatCompletionMessageWrapper;
import ru.rusguardian.service.ai.AiService;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.data.AIUserRequestService;
import ru.rusguardian.service.data.ChatService;
import ru.rusguardian.service.process.ProcessUpdateUserExtraBalance;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessChatTextPrompt {

    private static final String LIMIT_EXPIRED_MESSAGE = "Извините, но ваш лимит запросов на день для модели %s исчерпан. Купите подписку для увеличения лимита";
    private final AiService aiService;
    private final ChatService chatService;
    private final AIUserRequestService aiUserRequestService;
    private final ProcessUpdateUserExtraBalance updateChatBalance;

    public String process(Update update) {
        Chat chat = chatService.findById(TelegramUtils.getChatId(update));
        String prompt = TelegramUtils.getTextMessage(update);
        AIModel model = chat.getAiSettingsEmbedded().getAiActiveModel();
        float temperature = chat.getAiSettingsEmbedded().getTemperature();

        //1 Check for chat limit
        if (isChatLimitExpired(chat)) {
            return getLimitExpiredMessage(chat);
        }

        //2 Get Response from AI
        OpenAiApi.ChatCompletion chatCompletion = aiService.getTextPromptResponse(getChatCompletionMessages(chat, prompt), model, temperature);

        //3 Add request to database
        addAIUserRequestToDatabase(chat, chatCompletion);

        //4 Add response to chat messages
        addResponseMessageToChatList(chat, chatCompletion);

        //5 Update balance (for extra tokens)
//        updateChatBalance.process();


        //Choices depends on "n" in ChatCompletionRequest
        return chatCompletion.choices().get(0).message().content();
    }

    void isExpired() {

    }

    //1
    private String getLimitExpiredMessage(Chat chat) {
        return String.format(LIMIT_EXPIRED_MESSAGE, chat.getAiSettingsEmbedded().getAiActiveModel().getModelName());
    }

    //TODO
    private boolean isChatLimitExpired(Chat chat) {

        return false;
    }


    //TODO
    // 2
    private int getMaxTokens(Chat chat) {
        return 4096;
    }

    private List<OpenAiApi.ChatCompletionMessage> getPreviousChatCompletionMessages(Chat chat) {
        if (!chat.getAiSettingsEmbedded().isContextEnabled()) {
            return new ArrayList<>();
        }
        return chat.getMessages().stream().map(ChatCompletionMessageWrapper::getInner).toList();
    }

    private List<OpenAiApi.ChatCompletionMessage> getChatCompletionMessages(Chat chat, String currentText) {
        List<OpenAiApi.ChatCompletionMessage> messages = getPreviousChatCompletionMessages(chat);

        if (messages.isEmpty()) {
            AssistantRole role = chat.getAiSettingsEmbedded().getAssistantRole();
            messages.add(new OpenAiApi.ChatCompletionMessage(role.getPrompt(), OpenAiApi.ChatCompletionMessage.Role.SYSTEM));
        }
        messages.add(new OpenAiApi.ChatCompletionMessage(currentText, OpenAiApi.ChatCompletionMessage.Role.USER));
        return chatService.update(chat).getMessages().stream().map(ChatCompletionMessageWrapper::getInner).toList();
    }


    //3
    private void addAIUserRequestToDatabase(Chat chat, OpenAiApi.ChatCompletion chatCompletion) {
        AIUserRequest request = new AIUserRequest();
        request.setRequestTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(chatCompletion.created()), ZoneId.systemDefault()));
        request.setChat(chat);
        request.setPromptTokens(chatCompletion.usage().promptTokens());
        request.setCompletionTokens(chatCompletion.usage().completionTokens());
        request.setTotalTokens(chatCompletion.usage().totalTokens());
        //VULNERABILITY if model name unknown
        request.setAiModel(AIModel.getByModelName(chatCompletion.model()));

        aiUserRequestService.save(request);
    }


    // 4
    private void addResponseMessageToChatList(Chat chat, OpenAiApi.ChatCompletion chatCompletion) {
        chat.getMessages().add(new ChatCompletionMessageWrapper(chatCompletion.choices().get(0).message()));
    }

}
