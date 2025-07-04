package ru.yjailbir.shopappyandexpracticumsprint5.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.ProductEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByName(String name);
    Page<ProductEntity> findAllByName(String name, Pageable pageable);
}
