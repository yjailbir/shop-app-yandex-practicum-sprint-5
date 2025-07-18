package ru.yjailbir.shopappyandexpracticumsprint5.repository;

import reactor.core.publisher.Mono;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.ProductEntity;

import java.util.List;


public interface CustomRepository {
    Mono<Long> getMaxOrderId();
    Mono<List<ProductEntity>>  findByNameContainingIgnoreCasePaged(String name, int offset, int limit);
    Mono<List<ProductEntity>> findByNameContainingIgnoreCasePagedSorted(String name, int offset, int limit, String sortField);
    Mono<List<ProductEntity>> findPaged(int offset, int limit);
    Mono<List<ProductEntity>> findPagedSorted(int offset, int limit, String sortField);
}
