package ru.rusguardian.bot.command.prompts.image.img.inner;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.prompts.PromptCommand;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.process.prompt.ProcessPromptVision;
import ru.rusguardian.telegram.bot.util.util.FileUtils;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

@Component
@RequiredArgsConstructor
public class ClaudeSolveForImageCommand extends PromptCommand {

    private final ProcessPromptVision processPromptVision;

    @Override
    public CommandName getType() {
        return CommandName.CLAUDE_SOLVE_FOR_IMAGE;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        Chat chatOwner = getChatOwner(update);
        if (isChatLimitExpired(chatOwner, AIModel.STABLE_DIFFUSION)) {
            edit(getEditMessageWithResponse(TelegramUtils.getChatId(update), getChatLimitExpiredString(chatOwner), TelegramUtils.getMessageId(update)));
            return;
        }

        String prompt = "Найди решение";
        int replyId = editReplyToImageRequest(update, getChatLanguage(update));
        String initImageUrl = FileUtils.getFileUrlFromMessage(((Message) update.getCallbackQuery().getMessage()).getReplyToMessage(), bot).toString();
        Chat chat = getChatOwner(update);
        processPromptVision.process(chat, initImageUrl, prompt)
                .thenAccept(response -> editForPrompt(EditMessageText.builder().chatId(chat.getId()).text(response).messageId(replyId).build()))
                .exceptionally(e -> {
                    errorCommand.execute(update);
                    throw new RuntimeException(e);
                });
    }
}
