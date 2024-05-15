package ru.rusguardian.service.process;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.domain.user.SubscriptionEmbedded;
import ru.rusguardian.domain.user.UserBalanceEmbedded;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.data.ChatService;

@Service
@RequiredArgsConstructor
public class ProcessUpdateUserExtraBalance {

    private final ChatService chatService;

    //TODO
    public void process(Chat chat, OpenAiApi.ChatCompletion chatCompletion) {
        UserBalanceEmbedded balance = chat.getUserBalanceEmbedded();
        SubscriptionEmbedded subscription = chat.getSubscriptionEmbedded();


        AIModel model = AIModel.getByModelName(chatCompletion.model());

    }


}
