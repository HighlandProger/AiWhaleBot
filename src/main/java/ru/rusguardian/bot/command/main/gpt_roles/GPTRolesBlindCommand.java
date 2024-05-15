package ru.rusguardian.bot.command.main.gpt_roles;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.service.process.ProcessAssistantTypeChange;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.EditMessageUtil;
import ru.rusguardian.util.GPTRolesInlineKeyboardUtil;

import static ru.rusguardian.util.GPTRolesInlineKeyboardUtil.ASSISTANT_TYPE_ACTION;
import static ru.rusguardian.util.GPTRolesInlineKeyboardUtil.PAGE_ACTION;

@Component
@RequiredArgsConstructor
public class GPTRolesBlindCommand extends Command implements GPTRolesDescription {

    private final ProcessAssistantTypeChange processAssistantTypeChange;

    @Override
    public CommandName getType() {
        return CommandName.GPT_ROLES_BLIND;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {

        Chat chat = getChat(update);
        int page = 0;
        if (TelegramCallbackUtils.hasAction(update)) {
            String action = TelegramCallbackUtils.getArgFromCallback(update, 1);

            if (PAGE_ACTION.equals(action)) {
                page = Integer.parseInt(TelegramCallbackUtils.getArgFromCallback(update, 2));
            }
            if (ASSISTANT_TYPE_ACTION.equals(action)) {
                //TODO editMessageIfChangePossible
                processAssistantTypeChange.process(update, chat, bot);
                return;
            }
        }

        EditMessageText edit = EditMessageUtil.getMessageText(update, getDescriptionText(chat.getAiSettingsEmbedded().getAssistantRole()));
        edit.setReplyMarkup(GPTRolesInlineKeyboardUtil.getKeyboard(page, chat.getAiSettingsEmbedded().getAssistantRole()));

        bot.execute(edit);
    }


}
