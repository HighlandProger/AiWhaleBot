package ru.rusguardian.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AssistantRole;
import ru.rusguardian.telegram.bot.util.constants.Callback;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static ru.rusguardian.bot.command.service.CommandName.EMPTY;
import static ru.rusguardian.bot.command.service.CommandName.WELCOME;

public class GPTRolesInlineKeyboardUtil {

    public static final String PAGE_ACTION = "PAGE";
    public static final String ASSISTANT_TYPE_ACTION = "AS_TYPE";
    private static final String GPT_ROLES_PREFIX = CommandName.GPT_ROLES_BLIND_D.getBlindName();
    private static final List<List<AssistantRole>> assistantRolePages;
    private static final List<AssistantRole> assistantRoles = Arrays.stream(AssistantRole.values()).toList();

    static {
        assistantRolePages = IntStream.range(0, (assistantRoles.size() + 9) / 10)
                .mapToObj(i -> assistantRoles.subList(i * 10, Math.min(i * 10 + 10, assistantRoles.size())))
                .toList();
    }

    private GPTRolesInlineKeyboardUtil() {
    }

    public static InlineKeyboardMarkup getKeyboard(int page, AssistantRole type) {

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.addAll(getActionButtons(page).getKeyboard());
        rows.addAll(getAssistantTypeButtons(page, type).getKeyboard());
        rows.addAll(getBackButton().getKeyboard());

        markup.setKeyboard(rows);

        return markup;
    }

    public static int getPageNumberByAssistantRole(AssistantRole role) {
        Optional<Integer> index = IntStream.range(0, assistantRolePages.size())
                .filter(i -> assistantRolePages.get(i).contains(role))
                .boxed()
                .findFirst();

        if (index.isEmpty()) {
            throw new RuntimeException(String.format("Assistant role %s not found in pages %s", role, assistantRolePages));
        }
        return index.get();
    }

    private static InlineKeyboardMarkup getActionButtons(int page) {
        int nextPage = page == 9 ? 0 : page + 1;
        int prevPage = page == 0 ? 9 : page - 1;
        return ReplyMarkupUtil.getInlineKeyboard(new String[][][]{
                {{"⏪", getPageString(0)}, {"◀\uFE0F", getPageString(prevPage)},
                        {++page + "/10", EMPTY.getBlindName()},
                        {"▶\uFE0F", getPageString(nextPage)}, {"⏩", getPageString(9)}}
        });
    }

    private static InlineKeyboardMarkup getAssistantTypeButtons(int page, AssistantRole type) {
        List<AssistantRole> pagedList = getPaged(page);
        List<AssistantRole> firstColumn = pagedList.subList(0, 5);
        List<AssistantRole> secondColumn = pagedList.subList(5, 10);

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(getAssistantTypeButton(firstColumn.get(i), type));
            row.add(getAssistantTypeButton(secondColumn.get(i), type));
            buttons.add(row);
        }
        return ReplyMarkupUtil.getInlineKeyboardByButtons(buttons);
    }

    private static InlineKeyboardButton getAssistantTypeButton(AssistantRole type, AssistantRole existingType) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(existingType == type ? "✅ " + type.getName() : type.getName());
        button.setCallbackData(getAssistantTypeCallbackData(type));

        return button;
    }

    private static String getAssistantTypeCallbackData(AssistantRole type) {
        return Stream.of(GPT_ROLES_PREFIX, ASSISTANT_TYPE_ACTION, type.name()).collect(Collectors.joining(Callback.ARGS_DELIMITER.getValue()));
    }

    protected static List<AssistantRole> getPaged(int pageNumber) {
        return assistantRolePages.get(pageNumber);
    }

    private static InlineKeyboardMarkup getBackButton() {
        return ReplyMarkupUtil.getInlineKeyboard(new String[][][]{{{"Назад", WELCOME.getBlindName()}}});
    }

    protected static String getPageString(int pageNumber) {
        return Stream.of(GPT_ROLES_PREFIX, PAGE_ACTION, String.valueOf(pageNumber)).collect(Collectors.joining(Callback.ARGS_DELIMITER.getValue()));
    }
}
