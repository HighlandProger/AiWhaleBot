package ru.rusguardian.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.rusguardian.domain.UserSubscription;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    @Query("SELECT us FROM UserSubscription us WHERE us.chat.id = :id AND us.expirationTime > :currentTime")
    Optional<UserSubscription> getCurrentUserSubscription(@Param("id") Long id, @Param("currentTime") LocalDateTime currentTime);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM UserSubscription us WHERE us.chat.id = :id")
    void removeUserSubscriptions(@Param("id") Long id);
  }
