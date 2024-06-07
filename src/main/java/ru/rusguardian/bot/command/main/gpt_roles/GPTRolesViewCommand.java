package ru.rusguardian.bot.command.main.gpt_roles;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandMapping;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.domain.AssistantRoleData;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.telegram.bot.util.util.TelegramUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.SendMessageUtil;
import ru.rusguardian.util.GPTRolesInlineKeyboardService;

import java.text.MessageFormat;

@Component
@CommandMapping(viewCommands = {
        "\uD83C\uDFAD GPT - Роли",
        "\uD83C\uDFAD GPT - Roles",
        "\uD83C\uDFAD GPT - Rollen",
        "\uD83C\uDFAD GPT - rollari"})
@RequiredArgsConstructor
public class GPTRolesViewCommand extends Command {
    private static final String GPT_ROLES_INFO = "GPT_ROLES_INFO";
    private final GPTRolesInlineKeyboardService keyboardService;

    @Override
    public CommandName getType() {
        return CommandName.GPT_ROLES_VIEW;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {

        Chat chat = chatService.findById(TelegramUtils.getChatId(update));
        AILanguage language = chat.getAiSettingsEmbedded().getAiLanguage();
        AssistantRoleData role = assistantRoleDataService.getByChat(chat);

        SendMessage message = SendMessageUtil.getSimpleWithReplyOnLastUserMessage(update, getDescriptionText(role, language));
        message.setReplyMarkup(keyboardService.getKeyboard(0, role, language));
        sendMessage(message);
    }

    private String getDescriptionText(AssistantRoleData role, AILanguage language) {
        return MessageFormat.format(getTextByViewDataAndChatLanguage(GPT_ROLES_INFO, language), role.getViewName(), role.getDescription());
    }

}
