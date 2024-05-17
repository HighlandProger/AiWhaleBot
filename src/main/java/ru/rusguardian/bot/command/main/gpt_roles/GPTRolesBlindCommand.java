package ru.rusguardian.bot.command.main.gpt_roles;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AssistantRole;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.EditMessageUtil;
import ru.rusguardian.util.GPTRolesInlineKeyboardUtil;

import static ru.rusguardian.util.GPTRolesInlineKeyboardUtil.ASSISTANT_TYPE_ACTION;
import static ru.rusguardian.util.GPTRolesInlineKeyboardUtil.PAGE_ACTION;

@Component
@RequiredArgsConstructor
public class GPTRolesBlindCommand extends Command implements GPTRolesDescription {

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
                AssistantRole role = AssistantRole.valueOf(TelegramCallbackUtils.getArgFromCallback(update, 2));
                if (isRoleNotChanged(chat, role)) {
                    return;
                }
                if (isFreeSubscription(chat)) {
                    AnswerCallbackQuery answer = new AnswerCallbackQuery();
                    answer.setCallbackQueryId(TelegramUtils.getCallbackQueryId(update));
                    answer.setShowAlert(true);
                    answer.setText("\uD83C\uDFAD Выбор GPT ролей доступен только премиум подписчикам. Приобрести подписку вы можете командой /premium");
                    bot.execute(answer);
                    return;
                }

                chat.getAiSettingsEmbedded().setAssistantRole(role);
                chatService.update(chat);
                page = GPTRolesInlineKeyboardUtil.getPageNumberByAssistantRole(role);
            }
        }

        EditMessageText edit = EditMessageUtil.getMessageText(update, getDescriptionText(chat.getAiSettingsEmbedded().getAssistantRole()));
        edit.setReplyMarkup(GPTRolesInlineKeyboardUtil.getKeyboard(page, chat.getAiSettingsEmbedded().getAssistantRole()));

        bot.execute(edit);
    }

    private boolean isRoleNotChanged(Chat chat, AssistantRole role) {
        return chat.getAiSettingsEmbedded().getAssistantRole() == role;
    }

    private boolean isFreeSubscription(Chat chat) {
        return chat.getSubscriptionEmbedded().getSubscriptionInfo().getType() == SubscriptionType.FREE;
    }
}
