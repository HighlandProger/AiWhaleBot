package ru.rusguardian.service.process;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.util.ResourceTextUtil;

@Service
@RequiredArgsConstructor
public class ProcessGetTextLimitExpired {

    private static final String LIMIT_EXPIRED_FREE_NO_SUBSCRIPTION = "text/limit_expired_free_no_subscription/";
    private static final String LIMIT_EXPIRED = "text/limit_expired/";
    private final ProcessCheckChatChannelsSubscription checkChatChannelsSubscription;

    public String get(Chat chat, AIModel model) {
        AIModel.BalanceType balanceType = model.getBalanceType();
        if (balanceType == AIModel.BalanceType.GPT_3 && !checkChatChannelsSubscription.check(chat)){
            return ResourceTextUtil.getTextFromFileByChatLanguage(LIMIT_EXPIRED_FREE_NO_SUBSCRIPTION, chat);
        }else {
            return ResourceTextUtil.getTextFromFileByChatLanguage(LIMIT_EXPIRED, chat);
        }
    }
}
