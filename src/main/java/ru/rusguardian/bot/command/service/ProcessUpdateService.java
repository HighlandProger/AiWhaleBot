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
        log.info(update.toString());
        CommandName commandName = getCommandName(update);
        Command command = commandContainerService.getCommand(commandName);
        command.execute(update);
    }

    private CommandName getCommandName(Update update) {
        if (chatService.isUserBanned(TelegramUtils.getUserId(update))) {
            return USER_BANNED;
        }
        Optional<String> viewText = TelegramUtils.getViewTextMessage(update);
        Optional<String> callback = TelegramUtils.getCallback(update);
        if (isPublicMessage(update)) {
            if (isGroupOwnerNotFound(update)) {
                return GROUP_OWNER_NOT_FOUND;
            }
            if (!(viewText.isPresent() && viewText.get().startsWith(ASK_COMMAND) || update.hasCallbackQuery())) {
                return EMPTY;
            }
            viewText = getTrimmedViewTextOptional(viewText);
        }

        return getCommandNameByMessage(viewText, callback)
                .or(() -> findCommandByNextCommand(update)).orElse(PROMPT);
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

    private Optional<CommandName> getCommandNameByMessage(Optional<String> viewText, Optional<String> callback) {
        return getByBlindName(callback)
                .or(() -> getByViewName(viewText));
    }

    private Optional<CommandName> getByBlindName(Optional<String> callback) {
        if (callback.isEmpty()) {
            return Optional.empty();
        }
        return commandDispatcher.getByBlindStatic(callback.get()).or(() -> commandDispatcher.getByBlindVariable(callback.get()));
    }

    private Optional<CommandName> getByViewName(Optional<String> viewText) {
        if (viewText.isEmpty()) {
            return Optional.empty();
        }
        return commandDispatcher.getByViewStatic(viewText.get()).or(() -> commandDispatcher.getByViewVariable(viewText.get()));
    }

    private Optional<CommandName> findCommandByNextCommand(Update update) {
        Optional<Chat> chatOptional = chatService.findByIdOptional(TelegramUtils.getChatId(update));
        return chatOptional.map(Chat::getNextCommand);
    }

}