package ru.rusguardian.service.process.update;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.ai.dto.common.AiResponseCommonDto;
import ru.rusguardian.service.data.ChatService;
import ru.rusguardian.service.process.check.ProcessCheckChatRequestLimit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessUpdateUserExtraBalance {

    private final ChatService chatService;
    private final ProcessCheckChatRequestLimit checkChatRequestLimit;

    @Transactional
    public void updateGPT4ExtraBalance(Chat chat, AIModel model) {
        if (model != AIModel.GPT_4_OMNI) {throw new UnsupportedOperationException("Supported only GPT4, but used: " + model.name());}
        int allowedInSubscriptionCount = checkChatRequestLimit.getSubscriptionMinusUsedCount(chat, model);

        if (allowedInSubscriptionCount <= 0) {
            int gtp4ExtraRequests = chat.getUserBalanceEmbedded().getExtraGPT4Requests();
            chat.getUserBalanceEmbedded().setExtraGPT4Requests(--gtp4ExtraRequests);
            chatService.update(chat);
        }
    }

    @Transactional
    public void updateImageGenerationBalance(Chat chat, AIModel model){
        if (model.getBalanceType()!= AIModel.BalanceType.IMAGE){throw new UnsupportedOperationException("Unsupported balance type for image balance update: " + model.getBalanceType().name());}
        int allowedInSubscriptionCount = checkChatRequestLimit.getSubscriptionMinusUsedCount(chat, model);
        if (allowedInSubscriptionCount <= 0) {
            int imageExtraRequests = chat.getUserBalanceEmbedded().getExtraImageRequests();
            chat.getUserBalanceEmbedded().setExtraImageRequests(--imageExtraRequests);
            chatService.update(chat);
        }
    }

    @Transactional
    public void updateClaudeTokensBalance(Chat chat, AiResponseCommonDto dto){
        throw new RuntimeException("В РАЗРАБОТКЕ!!!");
//        int count = chat.getUserBalanceEmbedded().getClaudeTokens();
    }

}
