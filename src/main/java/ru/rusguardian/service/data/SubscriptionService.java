package ru.rusguardian.service.data;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import ru.rusguardian.domain.user.Subscription;
import ru.rusguardian.repository.SubscriptionRepository;
import ru.rusguardian.service.data.abstr.CrudService;

@Service
@RequiredArgsConstructor
public class SubscriptionService extends CrudService<Subscription, Long> {

    private final SubscriptionRepository repository;

    @Override
    protected Long getIdFromEntity(Subscription entity) {
        return entity.getId();
    }

    @Override
    protected JpaRepository<Subscription, Long> getRepository() {
        return repository;
    }
}
