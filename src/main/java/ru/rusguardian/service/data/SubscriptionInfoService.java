package ru.rusguardian.service.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.user.SubscriptionInfo;
import ru.rusguardian.repository.SubscriptionInfoRepository;
import ru.rusguardian.service.data.abstr.CrudService;

@Service
public class SubscriptionInfoService extends CrudService<SubscriptionInfo> {

    private SubscriptionInfoRepository repository;

    public SubscriptionInfoService(JpaRepository<SubscriptionInfo, Long> repository) {
        super(repository);
    }

    @Override
    protected Long getIdFromEntity(SubscriptionInfo entity) {
        return entity.getId();
    }

    public SubscriptionInfo getByType(SubscriptionType type) {
        return repository.getBySubscriptionType(type).orElseThrow();
    }
}
