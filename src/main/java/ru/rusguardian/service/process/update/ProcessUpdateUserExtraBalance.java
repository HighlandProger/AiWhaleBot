package ru.rusguardian.service.process.update;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.data.ChatService;
import ru.rusguardian.service.process.check.ProcessCheckChatRequestLimit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessUpdateUserExtraBalance {

    private final ChatService chatService;
    private final ProcessCheckChatRequestLimit checkChatRequestLimit;

    @Transactional
    public void updateUserTextExtraBalance(Chat chat, AIModel model) {
        AIModel.BalanceType balanceType = model.getBalanceType();
        if (!(balanceType == AIModel.BalanceType.GPT_4 || balanceType == AIModel.BalanceType.GPT_3)) {
            log.error("Not updatable extra balance for model {}", model);
            throw new RuntimeException("Update user text extra balance exception");
        }

        int allowedInSubscriptionCount = checkChatRequestLimit.getSubscriptionMinusUsedCount(chat, model);
        if (balanceType == AIModel.BalanceType.GPT_3) {
            return;
        }

        if (balanceType == AIModel.BalanceType.GPT_4 && (allowedInSubscriptionCount - 1 < 0)) {
            int gtp4ExtraRequests = chat.getUserBalanceEmbedded().getExtraGPT4Requests();
            chat.getUserBalanceEmbedded().setExtraGPT4Requests(--gtp4ExtraRequests);
        }
    }

    public void updateUserExtraBalance(Chat chat, AIModel model) {
        AIModel.BalanceType balanceType = model.getBalanceType();
        int allowedInSubscriptionCount = checkChatRequestLimit.getSubscriptionMinusUsedCount(chat, model);

        if (balanceType == AIModel.BalanceType.CLAUDE) {
            throw new RuntimeException("UNSUPPORTED BALANCE TYPE " + balanceType);
        }

        if (balanceType == AIModel.BalanceType.GPT_3) {
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

//    @Transactional
//    public void updateUserExtraBalance(Chat chat, OpenAiApi.ChatCompletion chatCompletion) {
//        UserBalanceEmbedded userBalance = chat.getUserBalanceEmbedded();
//        AIModel model = AIModel.getByModelName(chatCompletion.model());
//        AIModel.BalanceType balanceType = model.getBalanceType();
//        int allowedInSubscriptionCount = checkChatRequestLimit.getSubscriptionMinusUsedCount(chat, model);
//
//        if (balanceType == AIModel.BalanceType.GPT_3) {
//            return;
//        }
//
//        if (balanceType == AIModel.BalanceType.CLAUDE) {
//            int usedInRequestCount = chatCompletion.usage().totalTokens();
//            int overSubscriptionLimit = allowedInSubscriptionCount - usedInRequestCount;
//
//            if (overSubscriptionLimit < 0) {
//                int claudeBalance = Math.max(0, chat.getUserBalanceEmbedded().getClaudeTokens() + overSubscriptionLimit);
//                userBalance.setClaudeTokens(claudeBalance);
//                chatService.update(chat);
//            }
//            return;
//        }
//
//        if (balanceType == AIModel.BalanceType.GPT_4) {
//            if (allowedInSubscriptionCount - 1 < 0) {
//                int gtp4ExtraRequests = chat.getUserBalanceEmbedded().getExtraGPT4Requests();
//                chat.getUserBalanceEmbedded().setExtraGPT4Requests(--gtp4ExtraRequests);
//            }
//            return;
//        }
//        if (balanceType == AIModel.BalanceType.IMAGE) {
//            if (allowedInSubscriptionCount - 1 < 0) {
//                int imageExtraRequests = chat.getUserBalanceEmbedded().getExtraImageRequests();
//                chat.getUserBalanceEmbedded().setExtraImageRequests(--imageExtraRequests);
//            }
//            return;
//        }
//        if (balanceType == AIModel.BalanceType.MUSIC) {
//            if (allowedInSubscriptionCount - 1 < 0) {
//                int musicExtraRequests = chat.getUserBalanceEmbedded().getExtraSunoRequests();
//                chat.getUserBalanceEmbedded().setExtraSunoRequests(--musicExtraRequests);
//            }
//        }
//
//    }

}
