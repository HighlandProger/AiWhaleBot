package ru.rusguardian.bot.command.prompts.image.img.inner;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.prompts.PromptCommand;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.service.data.UserDataDtoService;
import ru.rusguardian.telegram.bot.util.util.FileUtils;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;

@Component
public class RequestForImageCommand extends PromptCommand {
    private static final String VIEW_DATA = "REQUEST_FOR_IMAGE";

    @Override
    public CommandName getType() {
        return CommandName.REQUEST_FOR_IMAGE;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {

        Chat chatOwner = getChatOwner(update);
        if (isChatLimitExpired(chatOwner, AIModel.STABLE_DIFFUSION)) {
            edit(getEditMessageWithResponse(TelegramUtils.getChatId(update), getChatLimitExpiredString(chatOwner), TelegramUtils.getMessageId(update)));
            return;
        }

        if(true){
            sendCurrentlyUnavailable(update);
            return;
        }

        String initImageUrl = FileUtils.getFileUrlFromMessage(((Message) update.getCallbackQuery().getMessage()).getReplyToMessage(), bot).toString();
        setNextCommand(update, CommandName.REQUEST_FOR_IMAGE_EXECUTE);
        editMessage(update, getTextByViewDataAndChatLanguage(VIEW_DATA, getChatLanguage(update)), null);
        UserDataDtoService.addImageUrl(TelegramUtils.getChatId(update), initImageUrl);
    }
}
