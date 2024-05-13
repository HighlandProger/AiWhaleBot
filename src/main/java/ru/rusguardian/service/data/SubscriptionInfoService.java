package ru.rusguardian.service.data;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.user.SubscriptionInfo;
import ru.rusguardian.repository.SubscriptionInfoRepository;
import ru.rusguardian.service.data.abstr.CrudService;

@Service
@RequiredArgsConstructor
public class SubscriptionInfoService extends CrudService<SubscriptionInfo, Long> {

    private final SubscriptionInfoRepository repository;

    public SubscriptionInfo getByType(SubscriptionType type) {
        return repository.getByType(type).orElseThrow();
    }

    @Override
    protected JpaRepository<SubscriptionInfo, Long> getRepository() {
        return repository;
    }

    @Override
    protected Long getIdFromEntity(SubscriptionInfo entity) {
        return entity.getId();
    }

}
