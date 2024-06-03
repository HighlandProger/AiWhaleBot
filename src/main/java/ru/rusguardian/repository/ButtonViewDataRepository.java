package ru.rusguardian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.constant.ai.AILanguage;
import ru.rusguardian.domain.ButtonViewData;

import java.util.List;

@Repository
public interface ButtonViewDataRepository extends JpaRepository<ButtonViewData, Long> {

    @Query("select value from ButtonViewData where commandName =:commandName AND aiLanguage =:aiLanguage")
    List<String> findByCommandNameAndLanguage(CommandName commandName, AILanguage aiLanguage);

}
