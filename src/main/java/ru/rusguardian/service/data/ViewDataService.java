package ru.rusguardian.service.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.domain.ViewData;
import ru.rusguardian.repository.ViewDataRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ViewDataService {

    private final ViewDataRepository viewDataRepository;

    public String getViewByNameAndLanguage(String name, AILanguage language) {
        if (language == AILanguage.RUSSIAN) {
            return viewDataRepository.findRussian(name);
        }
        if (language == AILanguage.ENGLISH) {
            return viewDataRepository.findEnglish(name);
        }
        if (language == AILanguage.GERMAN) {
            return viewDataRepository.findDeutsch(name);
        }
        if (language == AILanguage.UZBEK) {
            return viewDataRepository.findUzbek(name);
        }
        throw new IllegalArgumentException(language.name());
    }

    public ViewData findByName(String name) {
        return viewDataRepository.findById(name).orElseThrow();
    }

    public ViewData updateViewData(ViewData viewData) {
        findByName(viewData.getName());
        return viewDataRepository.save(viewData);
    }

    public List<ViewData> saveAll(List<ViewData> list) {
        return viewDataRepository.saveAll(list);
    }

    public List<ViewData> findAll() {
        return viewDataRepository.findAll();
    }
}
