package ru.rusguardian.service.process.get;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.SubscriptionInfo;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.data.ViewDataService;
import ru.rusguardian.service.process.check.ProcessCheckChatChannelsSubscription;

@Service
@RequiredArgsConstructor
public class ProcessGetTextLimitExpired {

    private static final String VIEW_DATA_FREE = "LIMIT_EXPIRED_FREE";
    private static final String VIEW_DATA_SUBSCRIPTION = "LIMIT_EXPIRED";
    private final ProcessCheckChatChannelsSubscription checkChatChannelsSubscription;
    private final ViewDataService viewDataService;

    public String get(Chat chat) {
        SubscriptionInfo subscription = chat.getSubscriptionEmbedded().getSubscriptionInfo();
        if (subscription.getType() == SubscriptionType.FREE && !checkChatChannelsSubscription.check(chat)) {
            return viewDataService.getViewByNameAndLanguage(VIEW_DATA_FREE, chat.getAiSettingsEmbedded().getAiLanguage());
        } else {
            return viewDataService.getViewByNameAndLanguage(VIEW_DATA_SUBSCRIPTION, chat.getAiSettingsEmbedded().getAiLanguage());
        }
    }
}
