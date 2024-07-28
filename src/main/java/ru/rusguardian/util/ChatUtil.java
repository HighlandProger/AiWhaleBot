package ru.rusguardian.util;

import ru.rusguardian.domain.AssistantRoleData;
import ru.rusguardian.domain.ChatCompletionMessage;
import ru.rusguardian.domain.Subscription;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.AIRequestSetting;
import ru.rusguardian.service.ai.constant.Role;
import ru.rusguardian.service.data.ChatCompletionMessageService;

import java.util.ArrayList;
import java.util.List;

public class ChatUtil {

    private ChatUtil() {
    }

    public static int getChatMaxTokens(Subscription subscription) {
        return subscription.getContextAndTokensXSize() * AIRequestSetting.TOKEN_SIZE.getValue();
    }

    public static List<ChatCompletionMessage> getLeadingChatCompletionMessages(Chat chat, ChatCompletionMessageService chatCompletionMessageService, AssistantRoleData role, Subscription subscription) {
        List<ChatCompletionMessage> messages = new ArrayList<>();
        messages.add(getChatSystemMessage(chat, role));
        messages.addAll(getPreviousChatCompletionMessages(chat, chatCompletionMessageService, subscription));
        return messages;
    }

    public static ChatCompletionMessage getChatSystemMessage(Chat chat, AssistantRoleData role) {
        return new ChatCompletionMessage(chat, Role.SYSTEM, role.getDescription());
    }

    public static List<ChatCompletionMessage> getPreviousChatCompletionMessages(Chat chat, ChatCompletionMessageService chatCompletionMessageService, Subscription subscription) {
        if (!chat.getAiSettingsEmbedded().isContextEnabled()) {
            return new ArrayList<>();
        }
        int messagesInList = AIRequestSetting.MESSAGES_IN_LIST.getValue() * subscription.getContextAndTokensXSize();
        List<ChatCompletionMessage> messages = chatCompletionMessageService.findByChatId(chat.getId());
        return messages.stream().skip(Math.max(0, messages.size() - messagesInList)).toList();
    }
}
