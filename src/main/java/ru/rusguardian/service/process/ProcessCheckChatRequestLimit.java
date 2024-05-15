package ru.rusguardian.service.process;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.domain.user.SubscriptionEmbedded;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.data.AIUserRequestService;
import ru.rusguardian.service.data.ChatService;

@Service
@RequiredArgsConstructor
public class ProcessCheckChatRequestLimit {

    private final ChatService chatService;
    private final AIUserRequestService aiUserRequestService;

    public int getCount(Chat chat, AIModel model) {
        SubscriptionEmbedded subscription = chat.getSubscriptionEmbedded();

        if (subscription.getSubscriptionInfo().getType() == SubscriptionType.FREE) {
            return getForFree();
        }
        if (subscription.getSubscriptionInfo().getType() == SubscriptionType.ALPHA) {
            return getForAlpha();
        }
        if (subscription.getSubscriptionInfo().getType() == SubscriptionType.PREMIUM) {
            return getForPremium();
        }
        if (subscription.getSubscriptionInfo().getType() == SubscriptionType.STARTER) {
            return getForStarter();
        }
        if (subscription.getSubscriptionInfo().getType() == SubscriptionType.ULTIMATE) {
            return getForUltimate();
        }
        throw new RuntimeException("UNKNOWN SUBSCRIPTION TYPE");
    }

    private int getForFree() {
        return 0;
    }

    private int getForAlpha() {
        return 0;

    }

    private int getForPremium() {
        return 0;

    }

    private int getForStarter() {
        return 0;

    }

    private int getForUltimate() {
        return 0;
    }
}
