package ru.rusguardian.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.rusguardian.domain.ButtonViewData;

import java.util.List;

@Repository
public interface ButtonViewDataRepository extends JpaRepository<ButtonViewData, Long> {

    @Query("select ruValue from ButtonViewData where name =:name")
    List<String> findRussian(String name, Sort sort);

    @Query("select enValue from ButtonViewData where name =:name")
    List<String> findEnglish(String name, Sort sort);

    @Query("select deValue from ButtonViewData where name =:name")
    List<String> findDeutsch(String name, Sort sort);

    @Query("select uzValue from ButtonViewData where name =:name")
    List<String> findUzbek(String name, Sort sort);

    List<ButtonViewData> findByName(String name, Sort sort);

    ButtonViewData findByNameAndButtonNumber(String name, Integer buttonNumber);
}
