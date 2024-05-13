package ru.rusguardian.bot.command.main.gpt_roles.process;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.main.gpt_roles.GPTRolesDescription;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

@Service
@RequiredArgsConstructor
public class ProcessAssistantTypeChange extends Command implements GPTRolesDescription {

    @Override
    public CommandName getType() {
        return CommandName.PROCESS_ASSISTANT_TYPE_CHANGE;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Chat chat = chatService.findById(TelegramUtils.getChatId(update));

    }
}
