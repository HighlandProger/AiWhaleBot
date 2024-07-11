package ru.rusguardian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.rusguardian.domain.AIUserRequest;
import ru.rusguardian.service.ai.constant.AIModel;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AIUserRequestRepository extends JpaRepository<AIUserRequest, Long> {

    @Query("SELECT COUNT(req.id) FROM AIUserRequest req " +
            "WHERE req.chat.id = :chatId " +
            "AND req.aiModel in :models " +
            "AND req.requestTime BETWEEN :startOfDay AND :endOfDay")
    Integer getDayRequestsCountByChatIdAndModels(@Param("chatId") Long chatId,
                                                 @Param("models") List<AIModel> models,
                                                 @Param("startOfDay") LocalDateTime startOfDay,
                                                 @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT COUNT(req.id) FROM AIUserRequest req " +
            "WHERE req.aiModel in :models " +
            "AND req.requestTime BETWEEN :startOfDay AND :endOfDay")
    Integer getRequestsCountByModelsAndTime(@Param("models") List<AIModel> models,
                                        @Param("startOfDay") LocalDateTime startTime,
                                        @Param("endOfDay") LocalDateTime endTime);

    @Query("SELECT SUM(req.totalTokens) FROM AIUserRequest req " +
            "WHERE req.chat.id = :chatId " +
            "AND req.aiModel in :models " +
            "AND req.requestTime BETWEEN :start AND :end")
    Integer getUsedTokensInSubscriptionTime(@Param("chatId") Long chatId,
                                            @Param("models") List<AIModel> models,
                                            @Param("start") LocalDateTime startOfDay,
                                            @Param("end") LocalDateTime endOfDay);
}
