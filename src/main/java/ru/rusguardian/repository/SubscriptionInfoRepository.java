package ru.rusguardian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.user.SubscriptionInfo;

import java.util.Optional;

@Repository
public interface SubscriptionInfoRepository extends JpaRepository<SubscriptionInfo, Long> {

    Optional<SubscriptionInfo> getByType(SubscriptionType type);
}
