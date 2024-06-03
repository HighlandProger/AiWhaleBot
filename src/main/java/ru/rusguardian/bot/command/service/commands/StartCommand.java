package ru.rusguardian.bot.command.service.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandMapping;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.process.create.ProcessCreateChat;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

@Component
@CommandMapping(viewCommands = {"/start"}, isViewVariable = true)
@Slf4j
@RequiredArgsConstructor
public class StartCommand extends Command {

    private final ProcessCreateChat processCreateChat;

    @Override
    public CommandName getType() {
        return CommandName.START_VIEW_D;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {

        Chat chat = chatService.findByIdOptional(TelegramUtils.getChatId(update)).orElseGet(() -> createNewUser(update));
        executeCommand(update, CommandName.WELCOME);
    }

    private Chat createNewUser(Update update) {
        return processCreateChat.process(update);
    }

}
