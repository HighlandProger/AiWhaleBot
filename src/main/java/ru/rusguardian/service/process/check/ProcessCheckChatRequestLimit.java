package ru.rusguardian.service.process.check;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.domain.user.UserBalanceEmbedded;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.data.AIUserRequestService;
import ru.rusguardian.service.data.UserSubscriptionService;
import ru.rusguardian.telegram.bot.util.constants.ChatMemberStatus;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

@Service
@RequiredArgsConstructor
public class ProcessCheckChatRequestLimit {

    private final AIUserRequestService aiUserRequestService;
    private final UserSubscriptionService userSubscriptionService;
    @Value("{telegram.channel.id}")
    private String telegramOwnerId;

    public int getSubscriptionMinusUsedCount(Chat chat, AIModel model) {
        AIModel.BalanceType balanceType = model.getBalanceType();
        int allowedBySubscriptionRequestCount = userSubscriptionService.getCurrentSubscription(chat.getId()).getAllowedBySubscriptionRequestCount(balanceType);
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

    public int getTotalAllowedCount(Chat chat, AIModel model, TelegramLongPollingBot bot) {
        UserBalanceEmbedded userBalance = chat.getUserBalanceEmbedded();
        int subscriptionMinusUsedCount = getSubscriptionMinusUsedCount(chat, model);
        AIModel.BalanceType balanceType = model.getBalanceType();

        if (balanceType == AIModel.BalanceType.GPT_3) {
            int extraForSubscriptionCount = isChatHasChannelSubscriptions(chat.getId(), bot) ? 5 : 0;
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

    public boolean isChatHasChannelSubscriptions(Long userId, TelegramLongPollingBot bot) {
        return TelegramUtils.getChatMemberStatus(telegramOwnerId, userId, bot) == ChatMemberStatus.MEMBER;
    }
}
