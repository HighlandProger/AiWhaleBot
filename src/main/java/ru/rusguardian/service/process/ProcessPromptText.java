package ru.rusguardian.service.process;

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
import ru.rusguardian.service.ai.constant.AIRequestSetting;
import ru.rusguardian.service.data.AIUserRequestService;
import ru.rusguardian.service.data.ChatService;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProcessPromptText {

    private static final String LIMIT_EXPIRED_MESSAGE = "Извините, но ваш лимит запросов на день для модели %s исчерпан. Купите подписку для увеличения лимита";
    private final AiService aiService;
    private final ChatService chatService;
    private final AIUserRequestService aiUserRequestService;
    private final ProcessCheckChatRequestLimit checkChatRequestLimit;
    private final ProcessUpdateUserExtraBalance updateChatBalance;
    private final ProcessGetTextLimitExpired getTextLimitExpired;

    public String prompt(Update update) {
        Chat chat = chatService.findById(TelegramUtils.getChatId(update));
        String prompt = TelegramUtils.getTextMessage(update);
        AIModel model = chat.getAiSettingsEmbedded().getAiActiveModel();
        float temperature = chat.getAiSettingsEmbedded().getTemperature();
        int maxTokens = chat.getSubscriptionEmbedded().getSubscriptionInfo().getContextAndTokensXSize() * AIRequestSetting.TOKEN_SIZE.getValue();

        //1 Check for chat limit
        if (isChatLimitExpired(chat, model)) {
            return getTextLimitExpired.get(chat, model);
        }

        //1.5 Get messages for context
        List<OpenAiApi.ChatCompletionMessage> completionMessages = getChatCompletionMessages(chat, prompt);

        //2 Get Response from AI
        OpenAiApi.ChatCompletion chatCompletion = aiService.getTextPromptResponse(completionMessages, model, temperature, maxTokens);

        //3 Add request to database
        addAIUserRequestToDatabase(chat, chatCompletion);

        //4 Add response to chat messages
        completionMessages.add(chatCompletion.choices().get(0).message());
        chat.setMessages(completionMessages);
        chatService.update(chat);

        //5 Update balance (for extra tokens)
        updateChatBalance.updateUserExtraBalance(chat, chatCompletion);

        //Choices depends on "n" in ChatCompletionRequest
        return chatCompletion.choices().get(0).message().content();
    }

    //1
    private boolean isChatLimitExpired(Chat chat, AIModel model) {
        return checkChatRequestLimit.getTotalAllowedCount(chat, model) < 0;
    }

    private List<OpenAiApi.ChatCompletionMessage> getPreviousChatCompletionMessages(Chat chat) {
        if (!chat.getAiSettingsEmbedded().isContextEnabled()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(chat.getMessages().stream().map(ChatCompletionMessageWrapper::getInner).toList());
    }

    private List<OpenAiApi.ChatCompletionMessage> getChatCompletionMessages(Chat chat, String currentText) {
        List<OpenAiApi.ChatCompletionMessage> messages = getPreviousChatCompletionMessages(chat);

        if (messages.isEmpty()) {
            AssistantRole role = chat.getAiSettingsEmbedded().getAssistantRole();
            messages.add(new OpenAiApi.ChatCompletionMessage(role.getPrompt(), OpenAiApi.ChatCompletionMessage.Role.SYSTEM));
        }

        List<OpenAiApi.ChatCompletionMessage> cuttedMessages = getCuttedMessages(chat, messages);
        cuttedMessages.add(new OpenAiApi.ChatCompletionMessage(currentText, OpenAiApi.ChatCompletionMessage.Role.USER));
        return cuttedMessages;
    }

    private List<OpenAiApi.ChatCompletionMessage> getCuttedMessages(Chat chat, List<OpenAiApi.ChatCompletionMessage> messages) {
        int messagesInList = AIRequestSetting.MESSAGES_IN_LIST.getValue() * chat.getSubscriptionEmbedded().getSubscriptionInfo().getContextAndTokensXSize();
        return new ArrayList<>(Stream.concat(
                messages.stream().limit(1),
                messages.stream().skip(Math.max(0, messages.size() - messagesInList))
        ).toList());
    }

    //3
    private void addAIUserRequestToDatabase(Chat chat, OpenAiApi.ChatCompletion chatCompletion) {
        AIUserRequest request = new AIUserRequest();
        request.setRequestTime(LocalDateTime.ofInstant(Instant.ofEpochSecond(chatCompletion.created()), ZoneId.systemDefault()));
        request.setChat(chat);
        request.setPromptTokens(chatCompletion.usage().promptTokens());
        request.setCompletionTokens(chatCompletion.usage().completionTokens());
        request.setTotalTokens(chatCompletion.usage().totalTokens());
        //VULNERABILITY if model name unknown
        request.setAiModel(AIModel.getByModelName(chatCompletion.model()));

        aiUserRequestService.save(request);
    }


}
