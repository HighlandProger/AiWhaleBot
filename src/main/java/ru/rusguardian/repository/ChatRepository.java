package ru.rusguardian.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.rusguardian.domain.user.Chat;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE ncs_bot.chats SET status = :status WHERE id = :id", nativeQuery = true)
    void updateUserStatus(@Param("id") Long id, @Param("status") String status);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE ncs_bot.chats SET next_command = :next_command WHERE id = :id", nativeQuery = true)
    void updateUserNextCommand(@Param("id") Long id, @Param("next_command") String completedCommandName);

    @Query(value = "SELECT message_thread_id FROM ncs_bot.chats WHERE id = :id", nativeQuery = true)
    Integer findMessageThreadIdById(@Param("id") Long id);

    @Query(value = "SELECT id FROM ncs_bot.chats WHERE message_thread_id = :message_thread_id", nativeQuery = true)
    Long findIdByMessageThreadId(@Param("message_thread_id") Integer messageThreadId);

    @Query("SELECT c FROM Chat c WHERE c.partnerEmbeddedInfo.invitedBy.id = :invitedById")
    List<Chat> findByInvitedBy(@Param("invitedById") Long id);

    @Query("SELECT isBanned FROM Chat c WHERE c.id = :id")
    Boolean isChatBanned(Long id);
}
