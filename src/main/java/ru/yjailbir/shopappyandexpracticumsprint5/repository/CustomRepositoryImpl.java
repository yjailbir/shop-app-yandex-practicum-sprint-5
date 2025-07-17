package ru.yjailbir.shopappyandexpracticumsprint5.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.ProductEntity;

@Repository
public class CustomRepositoryImpl implements CustomRepository {
    private final R2dbcEntityTemplate template;

    @Autowired
    public CustomRepositoryImpl(R2dbcEntityTemplate template) {
        this.template = template;
    }

    @Override
    public Mono<Long> getMaxOrderId() {
        return template
                .getDatabaseClient()
                .sql("SELECT MAX(id) FROM orders")
                .map(row -> row.get(0, Long.class))
                .one();
    }

    @Override
    public Flux<ProductEntity> findByNameContainingIgnoreCasePaged(String name, int offset, int limit) {
        return template
                .getDatabaseClient()
                .sql("""
                        SELECT * FROM products
                          WHERE LOWER(name) LIKE LOWER(CONCAT('%', :name, '%'))
                          ORDER BY id
                          LIMIT :limit OFFSET :offset
                        """)
                .bind("name", name)
                .bind("limit", limit)
                .bind("offset", offset)
                .map(
                        row -> new ProductEntity(
                                row.get("id", Long.class),
                                row.get("name", String.class),
                                row.get("description", String.class),
                                row.get("price", Integer.class),
                                row.get("img_name", String.class)
                        )
                )
                .all();
    }
}
