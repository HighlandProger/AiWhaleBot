package ru.rusguardian.service.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.repository.ViewDataRepository;

@Service
@RequiredArgsConstructor
public class ViewDataService {

    private final ViewDataRepository viewDataRepository;

    public String getViewByNameAndLanguage(String name, AILanguage language){
        if (language == AILanguage.RUSSIAN) {return viewDataRepository.findRussian(name);}
        if (language == AILanguage.ENGLISH) {return viewDataRepository.findEnglish(name);}
        if (language == AILanguage.GERMAN) {return viewDataRepository.findDeutsch(name);}
        if (language == AILanguage.UZBEK) {return viewDataRepository.findUzbek(name);}
        throw new IllegalArgumentException(language.name());
    }

}
