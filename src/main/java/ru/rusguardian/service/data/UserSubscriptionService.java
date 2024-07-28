package ru.rusguardian.service.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.Subscription;
import ru.rusguardian.domain.UserSubscription;
import ru.rusguardian.repository.UserSubscriptionRepository;
import ru.rusguardian.service.data.abstr.CrudService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSubscriptionService extends CrudService<UserSubscription, Long> {

    private final UserSubscriptionRepository repository;
    private final SubscriptionService subscriptionService;

    @Override
    protected JpaRepository<UserSubscription, Long> getRepository() {
        return repository;
    }

    @Override
    protected Long getIdFromEntity(UserSubscription entity) {
        return entity.getId();
    }

    public Optional<UserSubscription> getCurrentUserSubscriptionOptional(Long userId){
        return repository.getCurrentUserSubscription(userId, LocalDateTime.now());
    }

    public Subscription getCurrentSubscription(Long userId){
        Optional<UserSubscription> userSubscriptionOptional = getCurrentUserSubscriptionOptional(userId);
        if (userSubscriptionOptional.isEmpty()){
            return subscriptionService.findById(SubscriptionType.FREE);
        }
        return userSubscriptionOptional.get().getSubscription();
    }

    public UserSubscription getCurrentUserSubscription(Long userId){
        return getCurrentUserSubscriptionOptional(userId).orElseThrow();
    }

}
