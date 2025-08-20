package ru.yjailbir.shopappyandexpracticumsprint5.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.CartElementEntity;

@Repository
public interface CartElementCrudRepository extends ReactiveCrudRepository<CartElementEntity, Long> {
    Mono<CartElementEntity> findByProductId(Long productId);
    Mono<CartElementEntity> findByProductIdAndUserId(Long productId, Long userId);
    Flux<CartElementEntity> findAllByUserId(Long userId);
    Mono<Void> deleteAllByUserId(Long userId);
}
