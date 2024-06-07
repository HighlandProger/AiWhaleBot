package ru.rusguardian.bot.command.main.settings.inner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.AISettingsEmbedded;
import ru.rusguardian.domain.user.Chat;

@RequiredArgsConstructor
@Service
public class SwitchContextBlindCommand extends Command {
    @Override
    public CommandName getType() {
        return CommandName.SWITCH_CONTEXT_BLIND;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Chat chat = getChat(update);
        AISettingsEmbedded settings = chat.getAiSettingsEmbedded();
        settings.setContextEnabled(!settings.isContextEnabled());
        chatService.update(chat);

        commandContainerService.getCommand(CommandName.SETTINGS).execute(update);
    }
}
