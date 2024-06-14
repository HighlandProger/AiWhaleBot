package ru.rusguardian.service.process.check;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.domain.user.UserBalanceEmbedded;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.data.AIUserRequestService;

@Service
@RequiredArgsConstructor
public class ProcessCheckChatRequestLimit {

    private final AIUserRequestService aiUserRequestService;

    public int getSubscriptionMinusUsedCount(Chat chat, AIModel model) {
        AIModel.BalanceType balanceType = model.getBalanceType();
        int allowedBySubscriptionRequestCount = chat.getAllowedBySubscriptionRequestCount(balanceType);
        //For not limited
        if (allowedBySubscriptionRequestCount == -1) {
            return 1000000000;
        }

        if (balanceType == AIModel.BalanceType.GPT_3 || balanceType == AIModel.BalanceType.GPT_4 || balanceType == AIModel.BalanceType.IMAGE) {
            int dayUsedCount = aiUserRequestService.getDayRequestsCountByChatIdAndModels(chat.getId(), AIModel.getByBalanceType(balanceType));
            return allowedBySubscriptionRequestCount - dayUsedCount;
        }
        if (balanceType == AIModel.BalanceType.CLAUDE || balanceType == AIModel.BalanceType.MUSIC) {
            int monthUsedCount = aiUserRequestService.getInSubscriptionUsedCount(chat, balanceType);
            return allowedBySubscriptionRequestCount - monthUsedCount;
        }
        throw new RuntimeException("UNKNOWN BALANCE TYPE: " + balanceType);
    }

    public int getTotalAllowedCount(Chat chat, AIModel model) {
        UserBalanceEmbedded userBalance = chat.getUserBalanceEmbedded();
        int subscriptionMinusUsedCount = getSubscriptionMinusUsedCount(chat, model);
        AIModel.BalanceType balanceType = model.getBalanceType();

        if (balanceType == AIModel.BalanceType.GPT_3) {
            int extraForSubscriptionCount = isChatHasChannelSubscriptions(chat) ? 5 : 0;
            return subscriptionMinusUsedCount + extraForSubscriptionCount;
        }
        if (balanceType == AIModel.BalanceType.GPT_4) {
            return subscriptionMinusUsedCount + userBalance.getExtraGPT4Requests();
        }
        if (balanceType == AIModel.BalanceType.IMAGE) {
            return subscriptionMinusUsedCount + userBalance.getExtraImageRequests();
        }
        if (balanceType == AIModel.BalanceType.CLAUDE){
            return userBalance.getClaudeTokens();
        }

        return subscriptionMinusUsedCount;
    }

    //TODO functional checkSubscription
    public boolean isChatHasChannelSubscriptions(Chat chat) {
        return false;
    }
}
