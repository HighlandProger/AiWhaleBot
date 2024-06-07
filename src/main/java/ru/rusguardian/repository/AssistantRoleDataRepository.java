package ru.rusguardian.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.domain.AssistantRoleData;

import java.util.List;

@Repository
public interface AssistantRoleDataRepository extends JpaRepository<AssistantRoleData, String> {

    AssistantRoleData findByNameAndLanguage(String name, AILanguage language);

    List<AssistantRoleData> findByLanguage(AILanguage language);

    List<AssistantRoleData> findByLanguage(AILanguage language, Pageable pageable);


}
