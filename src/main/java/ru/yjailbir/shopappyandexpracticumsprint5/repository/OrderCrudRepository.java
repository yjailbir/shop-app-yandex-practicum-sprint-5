package ru.yjailbir.shopappyandexpracticumsprint5.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.OrderEntity;

@Repository
public interface OrderCrudRepository extends ReactiveCrudRepository<OrderEntity, Long> {
}
