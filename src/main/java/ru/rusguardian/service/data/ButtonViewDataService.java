package ru.rusguardian.service.data;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.domain.ButtonViewData;
import ru.rusguardian.exception.EmptyCollectionException;
import ru.rusguardian.repository.ButtonViewDataRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ButtonViewDataService {

    private final ButtonViewDataRepository buttonViewDataRepository;
    private static final Sort sort = Sort.by("buttonNumber").ascending();

    public List<String> getByNameAndLanguage(String name, AILanguage language) {
        List<String> viewDataButtons = switch (language) {
            case RUSSIAN -> buttonViewDataRepository.findRussian(name, sort);
            case ENGLISH -> buttonViewDataRepository.findEnglish(name, sort);
            case GERMAN -> buttonViewDataRepository.findDeutsch(name, sort);
            case UZBEK -> buttonViewDataRepository.findUzbek(name, sort);
            default -> throw new IllegalArgumentException(language.toString());
        };
        if (viewDataButtons.isEmpty()) {
            throw new EmptyCollectionException(String.format("ButtonViewData not found for name %s and language %s", name, language));
        }
        return viewDataButtons;
    }

    public List<ButtonViewData> getByName(String name) {
        return buttonViewDataRepository.findByName(name, sort);
    }

    public ButtonViewData update(ButtonViewData buttonViewData) {
        buttonViewDataRepository.findById(buttonViewData.getId()).orElseThrow(EntityNotFoundException::new);
        return buttonViewDataRepository.save(buttonViewData);
    }

    public List<ButtonViewData> saveAll(List<ButtonViewData> buttonViewDataList) {
        return buttonViewDataRepository.saveAll(buttonViewDataList);
    }

    public String getByNameLanguageAndButtonNumber(String name, AILanguage language, int buttonNumber) {
        return buttonViewDataRepository.findByNameAndButtonNumber(name, buttonNumber).getValueByLanguage(language);
    }

    public List<ButtonViewData> getAll() {
        return buttonViewDataRepository.findAll();
    }
}
