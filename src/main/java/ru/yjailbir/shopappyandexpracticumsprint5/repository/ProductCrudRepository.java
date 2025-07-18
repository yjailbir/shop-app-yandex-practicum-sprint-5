package ru.yjailbir.shopappyandexpracticumsprint5.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.ProductEntity;

@Repository
public interface ProductCrudRepository extends ReactiveCrudRepository<ProductEntity, Long> {
    Mono<ProductEntity> findByName(String name);
}
