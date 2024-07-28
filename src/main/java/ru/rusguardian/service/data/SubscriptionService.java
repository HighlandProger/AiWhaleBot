package ru.rusguardian.service.data;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.Subscription;
import ru.rusguardian.repository.SubscriptionRepository;
import ru.rusguardian.service.data.abstr.CrudService;

@Service
@RequiredArgsConstructor
public class SubscriptionService extends CrudService<Subscription, SubscriptionType> {

    private final SubscriptionRepository repository;

    @Override
    protected JpaRepository<Subscription, SubscriptionType> getRepository() {
        return repository;
    }

    @Override
    protected SubscriptionType getIdFromEntity(Subscription entity) {
        return entity.getType();
    }

}
