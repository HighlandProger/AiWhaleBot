package ru.rusguardian.service.data;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.SubscriptionInfo;
import ru.rusguardian.repository.SubscriptionInfoRepository;
import ru.rusguardian.service.data.abstr.CrudService;

@Service
@RequiredArgsConstructor
public class SubscriptionInfoService extends CrudService<SubscriptionInfo, SubscriptionType> {

    private final SubscriptionInfoRepository repository;

    @Override
    protected JpaRepository<SubscriptionInfo, SubscriptionType> getRepository() {
        return repository;
    }

    @Override
    protected SubscriptionType getIdFromEntity(SubscriptionInfo entity) {
        return entity.getType();
    }

}
