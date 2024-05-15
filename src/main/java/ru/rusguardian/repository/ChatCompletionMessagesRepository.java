package ru.rusguardian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.rusguardian.domain.user.ChatCompletionMessageWrapper;

@Repository
public interface ChatCompletionMessagesRepository extends JpaRepository<ChatCompletionMessageWrapper, Long> {
}
