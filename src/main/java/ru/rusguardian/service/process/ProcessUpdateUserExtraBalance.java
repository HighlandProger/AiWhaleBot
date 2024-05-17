package ru.rusguardian.service.process;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.domain.user.UserBalanceEmbedded;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.data.ChatService;

@Service
@RequiredArgsConstructor
public class ProcessUpdateUserExtraBalance {

    private final ChatService chatService;
    private final ProcessCheckChatRequestLimit checkChatRequestLimit;

    @Transactional
    public void updateUserExtraBalance(Chat chat, OpenAiApi.ChatCompletion chatCompletion) {
        UserBalanceEmbedded userBalance = chat.getUserBalanceEmbedded();
        AIModel model = AIModel.getByModelName(chatCompletion.model());
        AIModel.BalanceType balanceType = model.getBalanceType();
        int allowedInSubscriptionCount = checkChatRequestLimit.getSubscriptionMinusUsedCount(chat, model);

        if (balanceType == AIModel.BalanceType.GPT_3) {
            return;
        }

        if (balanceType == AIModel.BalanceType.CLAUDE) {
            int usedInRequestCount = chatCompletion.usage().totalTokens();
            int overSubscriptionLimit = allowedInSubscriptionCount - usedInRequestCount;

            if (overSubscriptionLimit < 0) {
                int claudeBalance = Math.max(0, chat.getUserBalanceEmbedded().getClaudeTokens() + overSubscriptionLimit);
                userBalance.setClaudeTokens(claudeBalance);
                chatService.update(chat);
            }
            return;
        }

        if (balanceType == AIModel.BalanceType.GPT_4) {
            if (allowedInSubscriptionCount - 1 < 0) {
                int gtp4ExtraRequests = chat.getUserBalanceEmbedded().getExtraGPT4Requests();
                chat.getUserBalanceEmbedded().setExtraGPT4Requests(--gtp4ExtraRequests);
            }
            return;
        }
        if (balanceType == AIModel.BalanceType.IMAGE) {
            if (allowedInSubscriptionCount - 1 < 0) {
                int imageExtraRequests = chat.getUserBalanceEmbedded().getExtraImageRequests();
                chat.getUserBalanceEmbedded().setExtraImageRequests(--imageExtraRequests);
            }
            return;
        }
        if (balanceType == AIModel.BalanceType.MUSIC) {
            if (allowedInSubscriptionCount - 1 < 0) {
                int musicExtraRequests = chat.getUserBalanceEmbedded().getExtraSunoRequests();
                chat.getUserBalanceEmbedded().setExtraSunoRequests(--musicExtraRequests);
            }
        }

    }

}
