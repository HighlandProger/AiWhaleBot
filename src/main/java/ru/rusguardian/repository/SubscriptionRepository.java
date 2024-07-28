package ru.rusguardian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.Subscription;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, SubscriptionType> {

    Optional<Subscription> getByType(SubscriptionType type);
}
