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

import java.util.Arrays;
import java.util.Optional;

import static ru.rusguardian.bot.command.service.CommandName.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProcessUpdateService {

    @Autowired
    private ChatService chatService;
    @Autowired
    @Lazy
    private CommandContainerService commandContainerService;

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
        if (TelegramUtils.getCallbackQueryData(update).startsWith(GPT_ROLES_BLIND.getBlindName()))
            return Optional.of(GPT_ROLES_BLIND);
        return Arrays.stream(CommandName.values())
                .filter(c -> c.getBlindName() != null)
                .filter(c -> c.getBlindName().equals(TelegramUtils.getCallbackQueryData(update)))
                .findFirst();
    }

    private Optional<CommandName> getByViewName(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return Optional.empty();
        if (update.getMessage().getText().startsWith(START.getViewName())) return Optional.of(START);
        return Arrays.stream(CommandName.values())
                .filter(c -> c.getViewName() != null)
                .filter(c -> c.getViewName().equals(TelegramUtils.getTextMessage(update)))
                .findFirst();
    }

    private Optional<CommandName> findCommandByNextCommand(Update update) {
        Optional<Chat> chatOptional = chatService.findByIdOptional(TelegramUtils.getChatId(update));
        return chatOptional.map(Chat::getNextCommand);
    }

}