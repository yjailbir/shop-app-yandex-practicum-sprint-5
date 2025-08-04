package ru.yjailbir.shopappyandexpracticumsprint5.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.OrderItemEntity;

@Repository
public interface OrderItemsCrudRepository extends ReactiveCrudRepository<OrderItemEntity, Long> {
    Flux<OrderItemEntity> findByOrderId(Long orderId);
}
