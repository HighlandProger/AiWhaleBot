package ru.rusguardian.service.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.repository.ButtonViewDataRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ButtonViewDataService {

    private final ButtonViewDataRepository buttonViewDataRepository;

    public List<String> getByCommandNameAndLanguage(CommandName commandName, AILanguage language) {
        return switch (language) {
            case RUSSIAN -> buttonViewDataRepository.findRussian(commandName);
            case ENGLISH -> buttonViewDataRepository.findEnglish(commandName);
            case GERMAN -> buttonViewDataRepository.findDeutsch(commandName);
            case UZBEK -> buttonViewDataRepository.findUzbek(commandName);
            default -> throw new IllegalArgumentException(language.toString());
        };
    }

}
