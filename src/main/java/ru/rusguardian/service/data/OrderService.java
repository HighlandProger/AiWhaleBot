package ru.rusguardian.service.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import ru.rusguardian.domain.Order;
import ru.rusguardian.repository.OrderRepository;
import ru.rusguardian.service.data.abstr.CrudService;

@Service
@Slf4j
public class OrderService extends CrudService<Order> {

    private OrderRepository orderRepository;

    public OrderService(JpaRepository<Order, Long> repository) {
        super(repository);
    }

    @Override
    protected Long getIdFromEntity(Order entity) {
        return entity.getId();
    }
}