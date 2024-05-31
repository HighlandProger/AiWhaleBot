package ru.rusguardian.util;

import ru.rusguardian.constant.ai.AssistantRole;
import ru.rusguardian.domain.ChatCompletionMessage;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.AIRequestSetting;
import ru.rusguardian.service.ai.constant.Role;
import ru.rusguardian.service.data.ChatCompletionMessageService;

import java.util.ArrayList;
import java.util.List;

public class ChatUtil {

    private ChatUtil(){}

    public static int getChatMaxTokens(Chat chat) {
        return chat.getSubscriptionEmbedded().getSubscriptionInfo().getContextAndTokensXSize() * AIRequestSetting.TOKEN_SIZE.getValue();
    }

    public static List<ChatCompletionMessage> getLeadingChatCompletionMessages(Chat chat, ChatCompletionMessageService chatCompletionMessageService) {
        List<ChatCompletionMessage> messages = new ArrayList<>();
        messages.addAll(getChatSystemMessage(chat));
        messages.addAll(getPreviousChatCompletionMessages(chat, chatCompletionMessageService));
        return messages;
    }

    private static List<ChatCompletionMessage> getChatSystemMessage(Chat chat) {
        AssistantRole assistantRole = chat.getAiSettingsEmbedded().getAssistantRole();
        return List.of(new ChatCompletionMessage(chat, Role.SYSTEM, assistantRole.getDescription()));
    }

    private static List<ChatCompletionMessage> getPreviousChatCompletionMessages(Chat chat, ChatCompletionMessageService chatCompletionMessageService) {
        if (!chat.getAiSettingsEmbedded().isContextEnabled()) {
            return new ArrayList<>();
        }
        int messagesInList = AIRequestSetting.MESSAGES_IN_LIST.getValue() * chat.getSubscriptionEmbedded().getSubscriptionInfo().getContextAndTokensXSize();
        List<ChatCompletionMessage> messages = chatCompletionMessageService.findByChatId(chat.getId());
        return messages.stream().skip(Math.max(0, messages.size() - messagesInList)).toList();
    }
}
