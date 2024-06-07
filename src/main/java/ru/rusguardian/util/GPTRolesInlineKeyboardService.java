package ru.rusguardian.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.domain.AssistantRoleData;
import ru.rusguardian.service.data.AssistantRoleDataService;
import ru.rusguardian.service.data.ViewDataService;
import ru.rusguardian.telegram.bot.util.constants.Callback;
import ru.rusguardian.telegram.bot.util.util.TelegramCallbackUtils;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.rusguardian.bot.command.service.CommandName.EMPTY;
import static ru.rusguardian.bot.command.service.CommandName.WELCOME;

@Service
@RequiredArgsConstructor
public class GPTRolesInlineKeyboardService {

    private final AssistantRoleDataService assistantRoleDataService;
    private final ViewDataService viewDataService;

    private static final int PAGE_SIZE = 10;
    public static final String PAGE_ACTION = "PAGE";
    public static final String ASSISTANT_TYPE_ACTION = "AS_TYPE";
    private static final String BACK_VIEW_DATA = "BACK";
    private static final String GPT_ROLES_PREFIX = CommandName.GPT_ROLES_BLIND_D.getBlindName();

    public InlineKeyboardMarkup getKeyboard(int page, AssistantRoleData role, AILanguage language) {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.addAll(getActionButtons(page).getKeyboard());
        rows.addAll(getAssistantTypeButtons(page, role, language).getKeyboard());
        rows.addAll(getBackButton(language).getKeyboard());

        markup.setKeyboard(rows);

        return markup;
    }

    public int getPageNumberByAssistantRoleAndLanguage(AssistantRoleData currentRole, AILanguage language) {
        List<AssistantRoleData> allEntitiesByLanguage = assistantRoleDataService.getAllByLanguage(language);
        return allEntitiesByLanguage.indexOf(currentRole) / PAGE_SIZE;
    }

    private InlineKeyboardMarkup getActionButtons(int page) {
        int nextPage = page == 9 ? 0 : page + 1;
        int prevPage = page == 0 ? 9 : page - 1;
        return ReplyMarkupUtil.getInlineKeyboard(new String[][][]{
                {{"⏪", getPageCallback(0)}, {"◀\uFE0F", getPageCallback(prevPage)},
                        {++page + "/10", EMPTY.getBlindName()},
                        {"▶\uFE0F", getPageCallback(nextPage)}, {"⏩", getPageCallback(9)}}
        });
    }

    private InlineKeyboardMarkup getAssistantTypeButtons(int page, AssistantRoleData currentType, AILanguage language) {
        List<AssistantRoleData> pagedList = getPaged(language, page);
        List<AssistantRoleData> firstColumn = pagedList.subList(0, 5);
        List<AssistantRoleData> secondColumn = pagedList.subList(5, 10);

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(getAssistantTypeButton(firstColumn.get(i), currentType));
            row.add(getAssistantTypeButton(secondColumn.get(i), currentType));
            buttons.add(row);
        }
        return ReplyMarkupUtil.getInlineKeyboardByButtons(buttons);
    }

    private InlineKeyboardButton getAssistantTypeButton(AssistantRoleData type, AssistantRoleData currentType) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(currentType.getName().equals(type.getName()) ? "✅ " + type.getViewName() : type.getViewName());
        button.setCallbackData(getAssistantTypeCallbackData(type, currentType));

        return button;
    }

    private String getAssistantTypeCallbackData(AssistantRoleData type, AssistantRoleData currentType) {
        if (currentType.equals(type)) {
            return EMPTY.name();
        }
        return Stream.of(GPT_ROLES_PREFIX, ASSISTANT_TYPE_ACTION, type.getName()).collect(Collectors.joining(Callback.ARGS_DELIMITER.getValue()));
    }

    protected List<AssistantRoleData> getPaged(AILanguage language, int pageNumber) {
        return assistantRoleDataService.getAllByLanguagePaged(language, pageNumber, PAGE_SIZE);
    }

    private InlineKeyboardMarkup getBackButton(AILanguage language) {
        String view = viewDataService.getViewByNameAndLanguage(BACK_VIEW_DATA, language);
        return ReplyMarkupUtil.getInlineKeyboard(new String[][][]{{{view, WELCOME.getBlindName()}}});
    }

    protected String getPageCallback(int pageNumber) {
        return TelegramCallbackUtils.getCallbackWithArgs(GPT_ROLES_PREFIX, PAGE_ACTION, String.valueOf(pageNumber));
    }
}
