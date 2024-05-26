package ru.rusguardian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.rusguardian.domain.ChatCompletionMessage;

import java.util.List;

@Repository
public interface ChatCompletionMessageRepository extends JpaRepository<ChatCompletionMessage, Long> {

    List<ChatCompletionMessage> findByChatIdOrderByIdAsc(Long chatId);

    @Transactional
    @Modifying
    @Query("UPDATE ChatCompletionMessage c SET c.message = :message WHERE c.chat.id = :chatId AND c.role = 'SYSTEM'")
    void updateChatSystemMessage(@Param("chatId") Long chatId, @Param("message") String systemMessage);

}
