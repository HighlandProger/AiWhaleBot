package ru.rusguardian.service.process.get;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.domain.user.SubscriptionEmbedded;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.data.ViewDataService;

@Service
@RequiredArgsConstructor
public class ProcessGetTextLimitExpired {

    private static final String VIEW_DATA_FREE = "LIMIT_EXPIRED_FREE";
    private static final String VIEW_DATA = "LIMIT_EXPIRED";
    private static final String VIEW_DATA_CLAUDE = "LIMIT_EXPIRED_CLAUDE";

    private final ViewDataService viewDataService;

    public String get(Chat chatOwner) {
        SubscriptionEmbedded subscriptionEmbedded = chatOwner.getSubscriptionEmbedded();
        AILanguage language = chatOwner.getAiSettingsEmbedded().getAiLanguage();
        AIModel.BalanceType balanceType = chatOwner.getAiSettingsEmbedded().getAiActiveModel().getBalanceType();

        if(balanceType == AIModel.BalanceType.CLAUDE){
            return viewDataService.getViewByNameAndLanguage(VIEW_DATA_CLAUDE, language);
        }

        if (subscriptionEmbedded.getSubscriptionInfo().getType() == SubscriptionType.FREE) {
            return viewDataService.getViewByNameAndLanguage(VIEW_DATA_FREE, language);
        }
        return viewDataService.getViewByNameAndLanguage(VIEW_DATA, language);
    }
}
