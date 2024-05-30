package ru.rusguardian.service.process.get;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.SubscriptionInfo;
import ru.rusguardian.domain.user.AISettingsEmbedded;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.domain.user.SubscriptionEmbedded;
import ru.rusguardian.domain.user.UserBalanceEmbedded;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.data.AIUserRequestService;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProcessGetTextUserAccount {

    private final AIUserRequestService userRequestService;

    public String get(Chat chat, String textPattern) {
        SubscriptionEmbedded subscription = chat.getSubscriptionEmbedded();
        SubscriptionInfo subscriptionInfo = subscription.getSubscriptionInfo();
        UserBalanceEmbedded userBalance = chat.getUserBalanceEmbedded();
        AISettingsEmbedded aiSettings = chat.getAiSettingsEmbedded();

        return MessageFormat.format(textPattern,
                String.valueOf(chat.getId()),
                subscriptionInfo.getType(),
                subscriptionInfo.getType() == SubscriptionType.FREE ? "-" : getSubscriptionExpirationDateString(subscription),
                subscription.getPurchaseType() == null ? "-" : subscription.getPurchaseType(),
                //----------------------------------------------------------
                getModelsPerDayUsage(chat.getId(), List.of(AIModel.GPT_3_5_TURBO, AIModel.GPT_3_5_TURBO_16_K)),
                getModelsPerDayUsage(chat.getId(), List.of(AIModel.GEMINI_1_5_PRO)),
                getModelsPerDayUsage(chat.getId(), AIModel.getByBalanceType(AIModel.BalanceType.GPT_4)),
                getAllowedImageCount(chat),
                userBalance.getClaudeTokens(),
                //----------------------------------------------------------
                userBalance.getExtraGPT4Requests(),
                userBalance.getExtraImageRequests(),
                //----------------------------------------------------------
                aiSettings.getAiActiveModel(),
                aiSettings.getAssistantRole().getName(),
                aiSettings.getTemperature(),
                aiSettings.isContextEnabled() ? "✅ Вкл" : "❌ Выкл",
                aiSettings.isVoiceResponseEnabled() ? "✅ Вкл" : "❌ Выкл",

                getHoursAndMinsOfDayRemaining());
    }

    private String getSubscriptionExpirationDateString(SubscriptionEmbedded subscription) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy 'г.'", new Locale("ru"));
        return subscription.getExpirationTime().format(formatter);
    }

    private int getModelsPerDayUsage(Long chatId, List<AIModel> models) {
        return userRequestService.getDayRequestsCountByChatIdAndModels(chatId, models);
    }

    private String getAllowedImageCount(Chat chat){
        int dayLimit = chat.getSubscriptionEmbedded().getSubscriptionInfo().getImageDayLimit();
        if (dayLimit == -1) return "+";
        return String.valueOf(chat.getSubscriptionEmbedded().getSubscriptionInfo().getImageDayLimit()
                - getModelsPerDayUsage(chat.getId(), AIModel.getByBalanceType(AIModel.BalanceType.IMAGE)));
    }

    private int getSongMonthRequests(Chat chat) {
        return userRequestService.getInSubscriptionUsedCount(chat, AIModel.BalanceType.MUSIC);
    }

    private String getHoursAndMinsOfDayRemaining() {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime endOfDay = LocalDateTime.MAX;

        LocalDateTime timeRemaining = endOfDay.minusHours(currentTime.getHour())
                .minusMinutes(currentTime.getMinute())
                .minusSeconds(currentTime.getSecond())
                .minusNanos(currentTime.getNano());

        int hoursRemaining = timeRemaining.getHour();
        int minutesRemaining = timeRemaining.getMinute();

        return String.format("%s ч. %s мин.", hoursRemaining, minutesRemaining);
    }
}
