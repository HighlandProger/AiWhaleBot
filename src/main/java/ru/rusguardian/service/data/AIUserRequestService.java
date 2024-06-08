package ru.rusguardian.service.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.AIUserRequest;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.repository.AIUserRequestRepository;
import ru.rusguardian.service.ai.constant.AIModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AIUserRequestService {

    private final AIUserRequestRepository repository;

    public int getDayRequestsCountByChatIdAndModel(Long chatId, AIModel model) {
        return getDayRequestsCountByChatIdAndModels(chatId, List.of(model));
    }

    public int getDayRequestsCountByChatIdAndModels(Long chatId, List<AIModel> models) {
        log.debug("Request to {} for subscription used count for chatId {} with {}", this, chatId, models);
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return repository.getDayRequestsCountByChatIdAndModels(chatId, models, startOfDay, endOfDay);
    }

    //MAY RETURN (CLAUDE TOKENS) OR (SONGS MONTH COUNT)
    public int getInSubscriptionUsedCount(Chat chat, AIModel.BalanceType balanceType) {
        log.debug("Request to {} for subscription used count for {} with {}", this, chat.getSubscriptionEmbedded(), balanceType);
        if (chat.getSubscriptionEmbedded().getSubscriptionInfo().getType() == SubscriptionType.FREE) {
            return 0;
        }
        LocalDateTime subscriptionStartTime = chat.getSubscriptionEmbedded().getPurchaseTime();
        LocalDateTime subscriptionEndTime = chat.getSubscriptionEmbedded().getExpirationTime();
        List<AIModel> claudeModels = AIModel.getByBalanceType(balanceType);

        Integer result = repository.getUsedTokensInSubscriptionTime(chat.getId(), claudeModels, subscriptionStartTime, subscriptionEndTime);
        return result == null ? 0 : result;
    }

    public AIUserRequest save(AIUserRequest request) {
        log.debug("Saving {}", request);
        return repository.save(request);
    }

    public List<AIUserRequest> saveAll(List<AIUserRequest> requests) {
        log.debug("Saving {}", requests);
        return repository.saveAll(requests);
    }

}
