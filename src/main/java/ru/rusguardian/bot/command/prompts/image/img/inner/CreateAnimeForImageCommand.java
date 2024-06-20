package ru.rusguardian.bot.command.prompts.image.img.inner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.prompts.image.img.SendPromptImageService;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.process.prompt.ProcessImagePrompt;
import ru.rusguardian.telegram.bot.util.util.FileUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateAnimeForImageCommand extends Command implements SendPromptImageService {

    private final ProcessImagePrompt processImagePrompt;

    @Override
    public CommandName getType() {
        return CommandName.CREATE_ANIME_FOR_IMAGE;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        int replyId = editReplyToImageRequest(update, getChatLanguage(update), viewDataService, bot);

        Chat chat = getChatOwner(update);
        String initImageUrl = FileUtils.getFileUrlFromMessage(((Message) update.getCallbackQuery().getMessage()).getReplyToMessage(), bot).toString();

        processImagePrompt.processAnimeImageUrl(chat, initImageUrl)
                .thenAccept(url -> sendPromptImage(url, String.valueOf(chat.getId()), replyId, bot))
                .exceptionally(e -> {
                    errorCommand.execute(update);
                    throw new RuntimeException(e);
                });
    }

}
