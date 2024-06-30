package ru.rusguardian.service.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.repository.ChatRepository;
import ru.rusguardian.service.data.abstr.CrudService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService extends CrudService<Chat, Long> {

    private final ChatRepository chatRepository;

    public void updateNextCommand(Long chatId, CommandName commandName) {
        log.debug("Updating chat with id = {} to next command = {}", chatId, commandName);
        findById(chatId);

        String name = null;
        if (commandName != null) name = commandName.name();
        chatRepository.updateUserNextCommand(chatId, name);
    }

    @Transactional
    public void setChatKicked(Long chatId) {
        try {
            chatRepository.setKicked(chatId);
        } catch (Exception e){
            log.error("Exception during set kicked to chat with id {}. Not modified", chatId);
        }
    }


    @Override
    protected Long getIdFromEntity(Chat entity) {
        return entity.getId();
    }

    @Override
    protected JpaRepository<Chat, Long> getRepository() {
        return chatRepository;
    }

    public List<Chat> findAllReferrals(Long chatId) {
        return chatRepository.findByInvitedBy(chatId);
    }

    public boolean isUserBanned(Long userId) {
        Boolean isBanned = chatRepository.isChatBanned(userId);
        return isBanned != null && isBanned;
    }

    public AILanguage getChatLanguage(Long userId) {
        return chatRepository.getLanguage(userId);
    }

    public List<Long> getAllNotKickedChatIds() {
        return chatRepository.findAllNotKickedIds();
    }
}