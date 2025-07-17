package ru.yjailbir.shopappyandexpracticumsprint5.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.CartElementEntity;

@Repository
public interface CartElementCrudRepository extends ReactiveCrudRepository<CartElementEntity, Long> {
    Flux<CartElementEntity> findByProductEntity_Id(Long productId);
}
