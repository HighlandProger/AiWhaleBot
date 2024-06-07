package ru.rusguardian.bot.command.main.gpt_roles;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.constant.user.SubscriptionType;
import ru.rusguardian.domain.AssistantRoleData;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.EditMessageUtil;
import ru.rusguardian.util.GPTRolesInlineKeyboardService;

import java.text.MessageFormat;

import static ru.rusguardian.util.GPTRolesInlineKeyboardService.ASSISTANT_TYPE_ACTION;
import static ru.rusguardian.util.GPTRolesInlineKeyboardService.PAGE_ACTION;

@Component
@RequiredArgsConstructor
public class GPTRolesBlindCommand extends Command {

    private final GPTRolesInlineKeyboardService keyboardService;
    private static final String GPT_ROLES_ANSWER_CALLBACK = "GPT_ROLES_ANSWER_CALLBACK";
    private static final String GPT_ROLES_INFO = "GPT_ROLES_INFO";


    @Override
    public CommandName getType() {
        return CommandName.GPT_ROLES_BLIND_D;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {

        Chat chat = getChat(update);
        AILanguage language = chat.getAiSettingsEmbedded().getAiLanguage();
        int page = 0;
        if (TelegramCallbackUtils.hasAction(update)) {
            String action = TelegramCallbackUtils.getArgFromCallback(update, 1);

            if (PAGE_ACTION.equals(action)) {
                page = Integer.parseInt(TelegramCallbackUtils.getArgFromCallback(update, 2));
            }
            if (ASSISTANT_TYPE_ACTION.equals(action)) {
                String assistantRoleName = TelegramCallbackUtils.getArgFromCallback(update, 2);
                AssistantRoleData role = assistantRoleDataService.getByNameAndLanguage(assistantRoleName, language);
                if (isRoleNotChanged(chat, role)) {
                    return;
                }
                if (isFreeSubscription(chat)) {
                    AnswerCallbackQuery answer = new AnswerCallbackQuery();
                    answer.setCallbackQueryId(TelegramUtils.getCallbackQueryId(update));
                    answer.setShowAlert(true);
                    answer.setText(getTextByViewDataAndChatLanguage(GPT_ROLES_ANSWER_CALLBACK, language));
                    bot.execute(answer);
                    return;
                }

                chat.getAiSettingsEmbedded().setAssistantRole(role);
                chatService.update(chat);
                page = keyboardService.getPageNumberByAssistantRoleAndLanguage(role, language);
            }
        }

        EditMessageText edit = EditMessageUtil.getMessageText(update, getDescriptionText(chat.getAiSettingsEmbedded().getAssistantRole(), language));
        edit.setReplyMarkup(keyboardService.getKeyboard(page, chat.getAiSettingsEmbedded().getAssistantRole(), language));

        bot.execute(edit);
    }

    private boolean isRoleNotChanged(Chat chat, AssistantRoleData role) {
        return chat.getAiSettingsEmbedded().getAssistantRole() == role;
    }

    private boolean isFreeSubscription(Chat chat) {
        return chat.getSubscriptionEmbedded().getSubscriptionInfo().getType() == SubscriptionType.FREE;
    }

    private String getDescriptionText(AssistantRoleData role, AILanguage language) {
        return MessageFormat.format(getTextByViewDataAndChatLanguage(GPT_ROLES_INFO, language), role.getViewName(), role.getDescription());
    }
}
