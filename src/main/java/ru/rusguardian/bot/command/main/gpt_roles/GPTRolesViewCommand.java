package ru.rusguardian.bot.command.main.gpt_roles;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandMapping;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.SendMessageUtil;
import ru.rusguardian.util.GPTRolesInlineKeyboardUtil;

@Component
@CommandMapping(viewCommands = {"\uD83C\uDFAD GPT - Роли"})
public class GPTRolesViewCommand extends Command implements GPTRolesDescription {
    @Override
    public CommandName getType() {
        return CommandName.GPT_ROLES_VIEW;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {

        Chat chat = chatService.findById(TelegramUtils.getChatId(update));
        SendMessage message = SendMessageUtil.getSimpleWithReplyOnLastUserMessage(update, getDescriptionText(chat.getAiSettingsEmbedded().getAssistantRole()));
        message.setReplyMarkup(GPTRolesInlineKeyboardUtil.getKeyboard(0, chat.getAiSettingsEmbedded().getAssistantRole()));
        sendMessage(message);
    }

}
