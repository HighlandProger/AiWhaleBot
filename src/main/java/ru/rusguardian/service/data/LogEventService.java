package ru.rusguardian.service.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.rusguardian.domain.LogEvent;
import ru.rusguardian.repository.LogEventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogEventService {

    private final LogEventRepository repository;

    public LogEvent create(LogEvent logEvent) {
        log.debug("Creating log event {}", logEvent);
        return repository.save(logEvent);
    }

    public List<LogEvent> saveAll(List<LogEvent> logEventList) {
        log.debug("Saving all log events. Count:{}", logEventList.size());
        return repository.saveAll(logEventList);
    }

    public boolean isChatHasEvent(Long chatId, String event) {
        return repository.getCountOfEventsForChat(chatId, event) > 0;
    }
}
