package ru.rusguardian.service.data;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import ru.rusguardian.domain.Order;
import ru.rusguardian.repository.OrderRepository;
import ru.rusguardian.service.data.abstr.CrudService;

@Service
@RequiredArgsConstructor
public class OrderService extends CrudService<Order, Long> {

    private final OrderRepository orderRepository;


    @Override
    protected Long getIdFromEntity(Order entity) {
        return entity.getId();
    }

    @Override
    protected JpaRepository<Order, Long> getRepository() {
        return orderRepository;
    }
}