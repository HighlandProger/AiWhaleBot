package ru.rusguardian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.rusguardian.domain.ViewData;

@Repository
public interface ViewDataRepository extends JpaRepository<ViewData, String> {

    @Query("select ruValue from ViewData where name =:name")
    String findRussian(String name);

    @Query("select enValue from ViewData where name =:name")
    String findEnglish(String name);

    @Query("select deValue from ViewData where name =:name")
    String findDeutsch(String name);

    @Query("select uzValue from ViewData where name =:name")
    String findUzbek(String name);
}
