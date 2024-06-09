package ru.rusguardian.service.data;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rusguardian.domain.ChatCompletionMessage;
import ru.rusguardian.repository.ChatCompletionMessageRepository;
import ru.rusguardian.service.data.abstr.CrudService;

import java.util.List;

//TODO PERFOMANCE FUTURE clear database
@RequiredArgsConstructor
@Service
public class ChatCompletionMessageService extends CrudService<ChatCompletionMessage, Long> {

    private final ChatCompletionMessageRepository repository;

    @Override
    protected JpaRepository<ChatCompletionMessage, Long> getRepository() {
        return repository;
    }

    @Override
    protected Long getIdFromEntity(ChatCompletionMessage entity) {
        return entity.getId();
    }

    public List<ChatCompletionMessage> findByChatId(Long chatId) {
        return repository.findByChatIdOrderByIdAsc(chatId);
    }

    @Transactional
    public void deleteContext(Long chatId) {
        repository.deleteByChatId(chatId);
    }
}
