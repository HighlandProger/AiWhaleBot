package ru.rusguardian.service.data;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.rusguardian.domain.TelegramData;
import ru.rusguardian.repository.TelegramDataRepository;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class TelegramDataService {

    private final Set<TelegramData> telegramDataSet = new HashSet<>();

    private final TelegramDataRepository telegramDataRepository;

    public TelegramDataService(TelegramDataRepository telegramDataRepository) {
        this.telegramDataRepository = telegramDataRepository;
    }

    @PostConstruct
    private void initData() {
        telegramDataSet.addAll(telegramDataRepository.findAll());
    }

    public TelegramData getDataByName(String name) {
        Optional<TelegramData> telegramDataOptional = telegramDataSet.stream().filter(s -> s.getName().equals(name)).findFirst();
        if (telegramDataOptional.isEmpty()) {
            log.error("Telegram data with name {} not found", name);
            throw new NoSuchElementException("Telegram data not found exception: " + name);
        }
        return telegramDataOptional.get();
    }

    public void updateData(TelegramData telegramData) {
        log.info("Updating telegram data: {}", telegramData);
        telegramDataSet.add(telegramData);

        telegramDataRepository.update(
                telegramData.getName(),
                telegramData.getTextMessage(),
                telegramData.getPhotoId(),
                telegramData.getVideoId(),
                telegramData.getStickerId());
    }

    public Set<TelegramData> getDataSet() {
        return this.telegramDataSet;
    }

}
