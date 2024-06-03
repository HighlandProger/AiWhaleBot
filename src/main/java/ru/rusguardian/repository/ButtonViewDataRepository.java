package ru.rusguardian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.ButtonViewData;

import java.util.List;

@Repository
public interface ButtonViewDataRepository extends JpaRepository<ButtonViewData, Long> {

    @Query("select ruValue from ButtonViewData where commandName =:commandName")
    List<String> findRussian(CommandName commandName);

    @Query("select enValue from ButtonViewData where commandName =:commandName")
    List<String> findEnglish(CommandName commandName);

    @Query("select deValue from ButtonViewData where commandName =:commandName")
    List<String> findDeutsch(CommandName commandName);

    @Query("select uzValue from ButtonViewData where commandName =:commandName")
    List<String> findUzbek(CommandName commandName);

}
