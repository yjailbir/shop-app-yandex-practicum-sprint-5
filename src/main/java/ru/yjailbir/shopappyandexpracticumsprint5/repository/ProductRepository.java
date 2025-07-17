package ru.yjailbir.shopappyandexpracticumsprint5.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.ProductEntity;

import java.util.Optional;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByName(String name);
}
