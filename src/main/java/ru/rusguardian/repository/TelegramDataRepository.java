package ru.rusguardian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.rusguardian.domain.TelegramData;

@Repository
public interface TelegramDataRepository extends JpaRepository<TelegramData, Long> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE ncs_bot.telegram_data SET text_message = :text_message, photo_id = :photo_id, sticker_id = :sticker_id, video_id = :video_id WHERE name = :name", nativeQuery = true)
    void update(@Param("name") String name,
                @Param("text_message") String textMessage,
                @Param("photo_id") String photoId,
                @Param("sticker_id") String stickerId,
                @Param("video_id") String videoId);

}
