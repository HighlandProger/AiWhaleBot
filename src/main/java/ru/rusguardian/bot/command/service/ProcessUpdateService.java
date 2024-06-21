package ru.rusguardian.bot.command.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.data.ChatService;
import ru.rusguardian.telegram.bot.util.constants.ChatType;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

import java.util.Optional;

import static ru.rusguardian.bot.command.service.CommandName.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProcessUpdateService {

    public static final String ASK_COMMAND = "/ask";

    @Autowired
    private ChatService chatService;
    @Autowired
    @Lazy
    private CommandContainerService commandContainerService;

    @Autowired
    private CommandDispatcher commandDispatcher;

    @Autowired
    private TelegramLongPollingBot bot;

    public void process(Update update) {
        log.info(getUpdateMessage(update));
        log.debug(update.toString());
        CommandName commandName = getCommandName(update);
        Command command = commandContainerService.getCommand(commandName);
        command.execute(update);
    }

    private String getUpdateMessage(Update update) {
        String viewOrBlind = TelegramUtils.getViewTextMessage(update).orElse(TelegramUtils.getCallbackQueryData(update));
        if (viewOrBlind == null) {
            viewOrBlind = update.hasMessage() && update.getMessage().hasPhoto() ? "PHOTO" : null;
        }
        return String.format("Message from %s : %s", TelegramUtils.getUserId(update), viewOrBlind);
    }

    private CommandName getCommandName(Update update) {
        if (chatService.isUserBanned(TelegramUtils.getUserId(update))) {
            return USER_BANNED;
        }

        return getByCallback(update)
                .or(() -> getByMessage(update))
                .or(() -> findCommandByNextCommand(update))
                .orElse(EXECUTE_TEXT_PROMPT);
    }

    private Optional<CommandName> getByMessage(Update update) {
        if (update.hasMessage() && update.getMessage().hasPhoto()) {
            return Optional.of(OBTAIN_IMAGE_REQUEST);
        }

        Optional<String> viewTextOptional = TelegramUtils.getViewTextMessage(update);
        if (viewTextOptional.isEmpty()) {
            return Optional.empty();
        }
        if (isPublicMessage(update)) {
            return getForPublic(update);
        }
        return getByTextMessage(viewTextOptional);
    }

    private Optional<CommandName> getForPublic(Update update) {
        Optional<String> viewTextOptional = TelegramUtils.getViewTextMessage(update);
        if (isGroupOwnerNotFound(update)) {
            return Optional.of(GROUP_OWNER_NOT_FOUND);
        }
        if (!(viewTextOptional.isPresent() && viewTextOptional.get().startsWith(ASK_COMMAND) || update.hasCallbackQuery())) {
            return Optional.of(EMPTY);
        }
        return getByTextMessage(getTrimmedViewTextOptional(viewTextOptional));
    }

    private boolean isPublicMessage(Update update) {
        return TelegramUtils.getChatType(update) != ChatType.PRIVATE;
    }

    private Optional<String> getTrimmedViewTextOptional(Optional<String> viewTextOptional) {
        if (viewTextOptional.isPresent()) {
            return Optional.of(viewTextOptional.get().substring(ASK_COMMAND.length()).trim());
        }
        return Optional.empty();
    }

    private boolean isGroupOwnerNotFound(Update update) {
        Long chatOwnerId = TelegramUtils.getChatOwnerId(TelegramUtils.getChatIdString(update), bot);
        Optional<Chat> chatOwner = chatService.findByIdOptional(chatOwnerId);
        return chatOwner.isEmpty();
    }

    private Optional<CommandName> getByCallback(Update update) {
        Optional<String> callback = TelegramUtils.getCallback(update);
        if (callback.isEmpty()) {
            return Optional.empty();
        }
        return commandDispatcher.getByBlindStatic(callback.get()).or(() -> commandDispatcher.getByBlindVariable(callback.get()));
    }

    private Optional<CommandName> getByTextMessage(Optional<String> viewTextOptional) {
        if (viewTextOptional.isEmpty()) {
            return Optional.empty();
        }
        return commandDispatcher.getByViewStatic(viewTextOptional.get()).or(() -> commandDispatcher.getByViewVariable(viewTextOptional.get()));
    }

    private Optional<CommandName> findCommandByNextCommand(Update update) {
        Optional<Chat> chatOptional = chatService.findByIdOptional(TelegramUtils.getChatId(update));
        return chatOptional.map(Chat::getNextCommand);
    }

}