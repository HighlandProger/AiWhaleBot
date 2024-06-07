package ru.rusguardian.service.data;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.domain.AssistantRoleData;
import ru.rusguardian.domain.user.Chat;
import ru.rusguardian.repository.AssistantRoleDataRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssistantRoleDataService {

    private final AssistantRoleDataRepository assistantRoleDataRepository;

    public AssistantRoleData save(AssistantRoleData assistantRoleData) {
        return assistantRoleDataRepository.save(assistantRoleData);
    }

    public AssistantRoleData getByNameAndLanguage(String name, AILanguage language) {
        return assistantRoleDataRepository.findByNameAndLanguage(name, language);
    }

    public AssistantRoleData getByChat(Chat chat) {
        String roleName = chat.getAiSettingsEmbedded().getAssistantRoleName();
        AILanguage language = chat.getAiSettingsEmbedded().getAiLanguage();
        return assistantRoleDataRepository.findByNameAndLanguage(roleName, language);
    }

    public List<AssistantRoleData> saveAll(List<AssistantRoleData> dataList) {
        return assistantRoleDataRepository.saveAll(dataList);
    }

    public List<AssistantRoleData> getAllByLanguage(AILanguage language) {
        return assistantRoleDataRepository.findByLanguage(language);
    }

    public List<AssistantRoleData> getAllByLanguagePaged(AILanguage language, int pageNumber, int pageSize) {
        return assistantRoleDataRepository.findByLanguage(language, PageRequest.of(pageNumber, pageSize));
    }

}
