package ru.yjailbir.shopappyandexpracticumsprint5.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.CartElementEntity;

import java.util.Optional;

@Repository
public interface CartElementRepository extends JpaRepository<CartElementEntity, Long> {
    Optional<CartElementEntity> findByProductEntity_Id(Long productId);
}
