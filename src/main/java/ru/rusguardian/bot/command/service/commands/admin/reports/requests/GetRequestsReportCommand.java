package ru.rusguardian.bot.command.service.commands.admin.reports.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.repository.AIUserRequestRepository;
import ru.rusguardian.service.ai.constant.AIModel;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetRequestsReportCommand extends Command {

    private final AIUserRequestRepository requestRepository;

    @Override
    public CommandName getType() {
        return CommandName.GET_REQUESTS_REPORT;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        editMessage(update, getText(requestRepository), ReplyMarkupUtil.getInlineKeyboard(getButtons()));
    }

    private String getText(AIUserRequestRepository requestRepository){
        String pattern = """
                Отчет по запросам за сутки:
                Всего: {0}
                GPT-3: {1}
                GPT-4: {2}
                CLAUDE: {3}
                DALLE: {4}
                STABLE_DIFFUSION: {5}
                MIDJOURNEY: {6}
                TTS-1: {7}
                WHISPER: {8}
                """;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime last24Hour = now.minusHours(24);
        return MessageFormat.format(pattern,
                requestRepository.getRequestsCountByModelsAndTime(Arrays.stream(AIModel.values()).toList(), last24Hour, now),
                requestRepository.getRequestsCountByModelsAndTime(List.of(AIModel.GPT_3_5_TURBO), last24Hour, now),
                requestRepository.getRequestsCountByModelsAndTime(List.of(AIModel.GPT_4_OMNI), last24Hour, now),
                requestRepository.getRequestsCountByModelsAndTime(AIModel.getByBalanceType(AIModel.BalanceType.CLAUDE), last24Hour, now),
                requestRepository.getRequestsCountByModelsAndTime(List.of(AIModel.DALL_E_3), last24Hour, now),
                requestRepository.getRequestsCountByModelsAndTime(List.of(AIModel.STABLE_DIFFUSION), last24Hour, now),
                requestRepository.getRequestsCountByModelsAndTime(List.of(AIModel.MIDJOURNEY), last24Hour, now),
                requestRepository.getRequestsCountByModelsAndTime(List.of(AIModel.TTS), last24Hour, now),
                requestRepository.getRequestsCountByModelsAndTime(List.of(AIModel.WHISPER), last24Hour, now));
    }

    private String[][][] getButtons(){
        return new String[][][]{
                {{CommandName.BACK.getViewName(), CommandName.CHOOSE_REPORT.getBlindName()}}
        };
    }
}
