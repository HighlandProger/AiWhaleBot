package ru.rusguardian.bot.command.prompts.image.img.inner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.prompts.PromptCommand;
import ru.rusguardian.bot.command.prompts.image.img.inner.service.ImageUrlDtoService;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.process.prompt.ProcessImagePrompt;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

@Component
@RequiredArgsConstructor
public class RequestForImageExecuteCommand extends PromptCommand {

    private final ProcessImagePrompt processImagePrompt;

    @Override
    public CommandName getType() {
        return CommandName.REQUEST_FOR_IMAGE_EXECUTE;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Chat chatOwner = getChatOwner(update);
        if (isChatLimitExpired(chatOwner, AIModel.STABLE_DIFFUSION)) {
            edit(getEditMessageWithResponse(TelegramUtils.getChatId(update), getChatLimitExpiredString(chatOwner), TelegramUtils.getMessageId(update)));
            return;
        }

        int replyId = sendReplyToImageRequest(update, getChatLanguage(update)).getMessageId();
        String prompt = TelegramUtils.getTextMessage(update);
        String initImageUrl = ImageUrlDtoService.getImageUrlAndRemove(TelegramUtils.getChatId(update));
        processImagePrompt.processImageChangeUrl(chatOwner, initImageUrl, prompt)
                .thenAccept(url -> sendPromptImage(url, String.valueOf(chatOwner.getId()), replyId))
                .exceptionally(e -> {
                    errorCommand.execute(update);
                    throw new RuntimeException(e);
                });
    }
}
