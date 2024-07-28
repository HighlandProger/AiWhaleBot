package ru.rusguardian.service.process.get;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.rusguardian.domain.Subscription;
import ru.rusguardian.domain.UserSubscription;
import ru.rusguardian.domain.user.AISettingsEmbedded;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.domain.user.UserBalanceEmbedded;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.data.AIUserRequestService;
import ru.rusguardian.service.data.AssistantRoleDataService;
import ru.rusguardian.service.data.UserSubscriptionService;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProcessGetTextUserAccount {

    private final AIUserRequestService userRequestService;
    private final UserSubscriptionService userSubscriptionService;
    private final AssistantRoleDataService assistantRoleDataService;

    public String get(Chat chat, String textPattern) {
        Optional<UserSubscription> userSubscriptionOptional = userSubscriptionService.getCurrentUserSubscriptionOptional(chat.getId());
        Subscription subscription = userSubscriptionService.getCurrentSubscription(chat.getId());
        UserBalanceEmbedded userBalance = chat.getUserBalanceEmbedded();
        AISettingsEmbedded aiSettings = chat.getAiSettingsEmbedded();
        String roleViewName = assistantRoleDataService.getByNameAndLanguage(aiSettings.getAssistantRoleName(), aiSettings.getAiLanguage()).getViewName();

        return MessageFormat.format(textPattern,
                String.valueOf(chat.getId()),
                subscription.getName(),
                userSubscriptionOptional.map(userSubscription -> getSubscriptionExpirationDateString(userSubscription.getExpirationTime())).orElse("-"),
                userSubscriptionOptional.isEmpty() ? "-" : userSubscriptionOptional.get().getPurchaseProvider(),
                //----------------------------------------------------------
                getModelsPerDayUsage(chat.getId(), List.of(AIModel.GPT_4_MINI)),
                getModelsPerDayUsage(chat.getId(), List.of(AIModel.GPT_3_5_TURBO)),
                getModelsPerDayUsage(chat.getId(), AIModel.getByBalanceType(AIModel.BalanceType.GPT_4)),
                getAllowedImageCount(chat.getId(), subscription),
                userBalance.getClaudeTokens(),
                //----------------------------------------------------------
                userBalance.getExtraGPT4Requests(),
                userBalance.getExtraImageRequests(),
                //----------------------------------------------------------
                aiSettings.getAiActiveModel(),
                roleViewName,
                aiSettings.getTemperature(),
                aiSettings.isContextEnabled() ? "✅" : "❌",
                aiSettings.isVoiceResponseEnabled() ? "✅" : "❌",

                getHoursAndMinsOfDayRemaining());
    }

    private String getSubscriptionExpirationDateString(LocalDateTime expirationTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy 'г.'", new Locale("ru"));
        return expirationTime.format(formatter);
    }

    private int getModelsPerDayUsage(Long chatId, List<AIModel> models) {
        return userRequestService.getDayRequestsCountByChatIdAndModels(chatId, models);
    }

    private String getAllowedImageCount(Long chatId, Subscription subscription) {
        int dayLimit = subscription.getImageDayLimit();
        if (dayLimit == -1) return "+";
        return String.valueOf(subscription.getImageDayLimit()
                - getModelsPerDayUsage(chatId, AIModel.getByBalanceType(AIModel.BalanceType.IMAGE)));
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
