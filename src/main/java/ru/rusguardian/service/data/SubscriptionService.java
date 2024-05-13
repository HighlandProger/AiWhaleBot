package ru.rusguardian.service.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import ru.rusguardian.domain.user.Subscription;
import ru.rusguardian.repository.SubscriptionRepository;
import ru.rusguardian.service.data.abstr.CrudService;

@Service
public class SubscriptionService extends CrudService<Subscription> {

    private SubscriptionRepository repository;

    public SubscriptionService(JpaRepository<Subscription, Long> repository) {
        super(repository);
    }

    @Override
    protected Long getIdFromEntity(Subscription entity) {
        return entity.getId();
    }
}
