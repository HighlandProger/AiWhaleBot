package ru.rusguardian.bot.command.main.menu_commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;

@Component
@RequiredArgsConstructor
public class StableDiffusionCommand extends Command {

    private static final String VIEW_DATA = "STABLE_DIFFUSION";

    @Override
    public CommandName getType() {
        return CommandName.STABLE_DIFFUSION;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        sendMessage(update, getTextByViewDataAndChatLanguage(VIEW_DATA, getChatOwner(update).getAiSettingsEmbedded().getAiLanguage()));
    }
}
