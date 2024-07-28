package ru.rusguardian.service.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rusguardian.domain.AIUserRequest;
import ru.rusguardian.domain.UserSubscription;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.repository.AIUserRequestRepository;
import ru.rusguardian.service.ai.constant.AIModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AIUserRequestService {

    private final AIUserRequestRepository repository;
    private final UserSubscriptionService userSubscriptionService;

    public int getDayRequestsCountByChatIdAndModels(Long chatId, List<AIModel> models) {
        log.debug("Request to {} for subscription used count for chatId {} with {}", this, chatId, models);
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return repository.getDayRequestsCountByChatIdAndModels(chatId, models, startOfDay, endOfDay);
    }

    //MAY RETURN (CLAUDE TOKENS) OR (SONGS MONTH COUNT)
    public int getInSubscriptionUsedCount(Chat chat, AIModel.BalanceType balanceType) {
        log.debug("Request to {} for subscription used count for chat {} with {}", this, chat.getId(), balanceType);
        Optional<UserSubscription> userSubscriptionOptional = userSubscriptionService.getCurrentUserSubscriptionOptional(chat.getId());
        if (userSubscriptionOptional.isEmpty()){return 0;}

        UserSubscription userSubscription = userSubscriptionOptional.get();

        LocalDateTime subscriptionStartTime = userSubscription.getStartTime();
        LocalDateTime subscriptionEndTime = userSubscription.getExpirationTime();
        List<AIModel> claudeModels = AIModel.getByBalanceType(balanceType);

        Integer result = repository.getUsedTokensInSubscriptionTime(chat.getId(), claudeModels, subscriptionStartTime, subscriptionEndTime);
        return result == null ? 0 : result;
    }

    public AIUserRequest save(AIUserRequest request) {
        log.debug("Saving {}", request);
        return repository.save(request);
    }
}
