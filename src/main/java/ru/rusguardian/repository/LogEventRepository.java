package ru.rusguardian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.rusguardian.domain.LogEvent;

@Repository
public interface LogEventRepository extends JpaRepository<LogEvent, Long> {

    @Transactional
    @Query(value = "select count(*) from log_events where chat_id =:chat_id and event =:event", nativeQuery = true)
    Long getCountOfEventsForChat(@Param("chat_id") Long chatId,
                                 @Param("event") String event);
}
