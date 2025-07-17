package ru.yjailbir.shopappyandexpracticumsprint5.repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.ProductEntity;


public interface CustomRepository {
    Mono<Long> getMaxOrderId();
    Flux<ProductEntity> findByNameContainingIgnoreCasePaged(String name, int offset, int limit);
}
