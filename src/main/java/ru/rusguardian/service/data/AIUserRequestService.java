package ru.rusguardian.service.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.rusguardian.domain.AIUserRequest;
import ru.rusguardian.repository.AIUserRequestRepository;
import ru.rusguardian.service.ai.constant.AIModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIUserRequestService {

    private final AIUserRequestRepository repository;

    public int getDayRequestsCountByChatIdAndModel(Long chatId, AIModel model) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return repository.getDayRequestsCountByChatIdAndModel(chatId, model, startOfDay, endOfDay);
    }

    public AIUserRequest save(AIUserRequest request) {
        log.debug("Saving {}", request);
        return repository.save(request);
    }
}
