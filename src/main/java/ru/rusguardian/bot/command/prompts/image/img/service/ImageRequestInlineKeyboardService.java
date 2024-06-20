package ru.rusguardian.bot.command.prompts.image.img.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.service.data.ButtonViewDataService;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;

import java.util.List;

import static ru.rusguardian.bot.command.service.CommandName.*;

@Service
@RequiredArgsConstructor
public class ImageRequestInlineKeyboardService {

    private static final String BUTTON_VIEW_DATA = "IMAGE_REQUEST";

    private final ButtonViewDataService buttonViewDataService;

    public InlineKeyboardMarkup getMarkup(AILanguage language) {
        return ReplyMarkupUtil.getInlineKeyboard(getButtons(language));
    }

    private String[][][] getButtons(AILanguage language) {
        List<String> viewButtons = buttonViewDataService.getByNameAndLanguage(BUTTON_VIEW_DATA, language);

        return new String[][][]{
                {{viewButtons.get(0), CREATE_ANIME_FOR_IMAGE.getBlindName()}, {viewButtons.get(1), CLAUDE_SOLVE_FOR_IMAGE.getBlindName()}},
                {{viewButtons.get(2), REQUEST_FOR_IMAGE.getBlindName()}, {viewButtons.get(3), GET_KNOWN_FOR_IMAGE.getBlindName()}},
                {{viewButtons.get(4), REMOVE_BACKGROUND_FOR_IMAGE.getBlindName()}, {viewButtons.get(5), CHANGE_BACKGROUND_FOR_IMAGE.getBlindName()}},
                {{viewButtons.get(6), REMOVE_TEXT_FOR_IMAGE.getBlindName()}, {viewButtons.get(7), SUPER_QUALITY_FOR_IMAGE.getBlindName()}},
                {{viewButtons.get(8), GPT_4_VISION_FOR_IMAGE.getBlindName()}},
                {{viewButtons.get(9), HELP_FOR_IMAGE.getBlindName()}}
        };
    }


}
