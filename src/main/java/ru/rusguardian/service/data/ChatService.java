package ru.rusguardian.service.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.repository.ChatRepository;
import ru.rusguardian.service.data.abstr.CrudService;

@Service
@Slf4j
public class ChatService extends CrudService<Chat> {

    private ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        super(chatRepository);
    }

    @Override
    protected Long getIdFromEntity(Chat entity) {
        return entity.getId();
    }

    public void updateNextCommand(Long chatId, CommandName commandName) {
        log.debug("Updating chat with id = {} to next command = {}", chatId, commandName);
        findById(chatId);

        String name = null;
        if (commandName != null) name = commandName.name();
        chatRepository.updateUserNextCommand(chatId, name);
    }

}