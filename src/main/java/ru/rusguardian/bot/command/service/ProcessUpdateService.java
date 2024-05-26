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
import java.util.List;
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
        String callback = TelegramUtils.getCallbackQueryData(update);

        Optional<CommandName> blindDifOptional = getByBlindDiff(callback);
        if (blindDifOptional.isPresent()) {
            return blindDifOptional;
        }

        return Arrays.stream(CommandName.values())
                .filter(c -> c.getBlindName() != null)
                .filter(c -> c.getBlindName().equals(callback))
                .findFirst();
    }

    private Optional<CommandName> getByBlindDiff(String callback) {
        List<CommandName> differableBlinds = Arrays.stream(values()).filter(c -> c.name().endsWith("BLIND_D")).toList();
        return differableBlinds.stream().filter(d -> callback.startsWith(d.getBlindName())).findFirst();
    }

    private Optional<CommandName> getByViewName(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return Optional.empty();
        String text = TelegramUtils.getTextMessage(update);

        Optional<CommandName> viewDifOptional = getByViewDiff(text);
        if (viewDifOptional.isPresent()) {
            return viewDifOptional;
        }

        if (update.getMessage().isCommand()) {
            return Optional.of(TEXT_COMMAND_DISTRIBUTOR);
        }

        if (text.startsWith(START.getViewName())) return Optional.of(START);
        return Arrays.stream(CommandName.values())
                .filter(c -> c.getViewName() != null)
                .filter(c -> c.getViewName().equals(text))
                .findFirst();
    }

    private Optional<CommandName> getByViewDiff(String callback) {
        List<CommandName> differableViews = Arrays.stream(values()).filter(c -> c.name().endsWith("VIEW_D")).toList();
        return differableViews.stream().filter(d -> callback.startsWith(d.getViewName())).findFirst();
    }

    private Optional<CommandName> findCommandByNextCommand(Update update) {
        Optional<Chat> chatOptional = chatService.findByIdOptional(TelegramUtils.getChatId(update));
        return chatOptional.map(Chat::getNextCommand);
    }

}