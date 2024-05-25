package ru.rusguardian.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.rusguardian.domain.Order;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.isPurchased = true WHERE o.id = :orderId")
    void setOrderAsPurchased(Long orderId);

    List<Order> findOrdersByIsPurchasedAndChatIdIn(boolean isPurchased, List<Long> ids);
}
