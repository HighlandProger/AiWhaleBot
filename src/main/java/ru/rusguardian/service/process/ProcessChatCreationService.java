package ru.rusguardian.service.process;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.constant.ai.AIUserModel;
import ru.rusguardian.constant.ai.AssistantRole;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.domain.user.Subscription;
import ru.rusguardian.domain.user.SubscriptionInfo;
import ru.rusguardian.service.data.ChatService;
import ru.rusguardian.service.data.SubscriptionInfoService;
import ru.rusguardian.service.data.SubscriptionService;
import ru.rusguardian.telegram.bot.util.util.TelegramStartInfoUtils;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProcessChatCreationService {

    private final SubscriptionInfoService subscriptionInfoService;
    private final SubscriptionService subscriptionService;
    private final ChatService chatService;

    @Transactional
    public Chat process(Update update) {

        Chat chat = new Chat();
        chat.setId(TelegramUtils.getChatId(update));
        chat.setUsername(TelegramUtils.getUsername(update));
        chat.setTelegramFirstName(TelegramUtils.getFirstname(update));
        chat.setTelegramLastName(TelegramUtils.getLastname(update));
        chat.setRegistrationTime(LocalDateTime.now());
        chat.setAdmin(false);
        chat.setStartInfo(TelegramUtils.getStartInfo(update));
        chat.setTrafficLink(TelegramStartInfoUtils.getTrafficLink(update).orElse(null));
        chat.setCampaign(TelegramStartInfoUtils.getCampaign(update).orElse(null));
        chat.setAssistantRole(AssistantRole.USUAL);
        chat.setSubscription(getFreeSubscription());
        chat.setAiUserModel(AIUserModel.GPT_3_5_TURBO);
        chat.setTemperature(1.0f);
        chat.setAiLanguage(AILanguage.RUSSIAN);

        chat.setClaudeTokens(0);
        chat.setExtraGPT4Requests(0);
        chat.setExtraImageRequests(0);
        chat.setExtraSunoRequests(0);

        chat.setContextEnabled(true);
        chat.setVoiceResponseEnabled(false);

        return chatService.save(chat);
    }

    private Subscription getFreeSubscription() {
        SubscriptionInfo subscriptionInfo = subscriptionInfoService.getByType(SubscriptionType.FREE);

        Subscription subscription = new Subscription();
        subscription.setSubscriptionInfo(subscriptionInfo);

        return subscriptionService.save(subscription);
    }
}
