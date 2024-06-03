package ru.rusguardian.bot.command.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.data.ChatService;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

import java.util.Optional;

import static ru.rusguardian.bot.command.service.CommandName.PROMPT;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProcessUpdateService {

    @Autowired
    private ChatService chatService;
    @Autowired
    @Lazy
    private CommandContainerService commandContainerService;

    @Autowired
    private CommandDispatcher commandDispatcher;

    public void process(Update update) {
        log.info(update.toString());
        CommandName commandName = getCommandName(update);
        Command command = commandContainerService.getCommand(commandName);
        command.execute(update);
    }

    private CommandName getCommandName(Update update) {
        return getCommandNameByMessage(update)
                .or(() -> findCommandByNextCommand(update)).orElse(PROMPT);
    }

    private Optional<CommandName> getCommandNameByMessage(Update update) {
        return getByBlindName(update)
                .or(() -> getByViewName(update));
    }

    private Optional<CommandName> getByBlindName(Update update) {
        if (!update.hasCallbackQuery()) return Optional.empty();
        String callback = TelegramUtils.getCallbackQueryData(update);

        return commandDispatcher.getByBlindStatic(callback).or(() -> commandDispatcher.getByBlindVariable(callback));
    }

    private Optional<CommandName> getByViewName(Update update) {
        if (!isUpdateHasViewText(update)) return Optional.empty();
        String text = TelegramUtils.getTextMessage(update);

        return commandDispatcher.getByViewStatic(text).or(() -> commandDispatcher.getByViewVariable(text));
    }

    private boolean isUpdateHasViewText(Update update) {
        return update.hasMessage() && (update.getMessage().hasText() || update.getMessage().getCaption() != null);
    }

    private Optional<CommandName> findCommandByNextCommand(Update update) {
        Optional<Chat> chatOptional = chatService.findByIdOptional(TelegramUtils.getChatId(update));
        return chatOptional.map(Chat::getNextCommand);
    }

}