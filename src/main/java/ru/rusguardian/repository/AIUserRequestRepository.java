package ru.rusguardian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.rusguardian.domain.AIUserRequest;
import ru.rusguardian.service.ai.constant.AIModel;

import java.time.LocalDateTime;

@Repository
public interface AIUserRequestRepository extends JpaRepository<AIUserRequest, Long> {


    @Query("SELECT COUNT(req) FROM AIUserRequest req " +
            "WHERE req.chat.id = :chatId " +
            "AND req.aiModel = :model " +
            "AND req.requestTime BETWEEN :startOfDay AND :endOfDay")
    int getDayRequestsCountByChatIdAndModel(@Param("chatId") Long chatId,
                                            @Param("model") AIModel model,
                                            @Param("startOfDay") LocalDateTime startOfDay,
                                            @Param("endOfDay") LocalDateTime endOfDay);
}
