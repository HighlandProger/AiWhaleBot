package ru.rusguardian.bot.command.prompts.image.img;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.prompts.PromptCommand;
import ru.rusguardian.bot.command.service.CommandName;

@RequiredArgsConstructor
@Component
public class ObtainImageRequestCommand extends PromptCommand {
    @Override
    public CommandName getType() {
        return CommandName.OBTAIN_IMAGE_REQUEST;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        if(!(update.hasMessage() && update.getMessage().hasPhoto())) throw new RuntimeException();

        sendQuickReply(update);
    }

    private void sendQuickImageReply(){

    }
}
