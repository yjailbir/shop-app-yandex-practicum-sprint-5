package ru.yjailbir.shopappyandexpracticumsprint5.repository;

import io.r2dbc.spi.Readable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.yjailbir.shopappyandexpracticumsprint5.entity.ProductEntity;

import java.util.List;

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
    public Mono<List<ProductEntity>> findByNameContainingIgnoreCasePaged(String name, int offset, int limit) {
        String sql = String.format(
                "SELECT * FROM products WHERE LOWER(name) LIKE LOWER(CONCAT('%%', '%s', '%%')) ORDER BY id LIMIT %d OFFSET %d",
                name, limit, offset
        );

        return template
                .getDatabaseClient()
                .sql(sql)
                .map(this::mapRowToProductEntity)
                .all()
                .collectList();
    }

    @Override
    public Mono<List<ProductEntity>> findByNameContainingIgnoreCasePagedSorted(String name, int offset, int limit, String sortField) {
        String sql = String.format(
                "SELECT * FROM products WHERE LOWER(name) LIKE LOWER(CONCAT('%%', '%s', '%%')) ORDER BY %s LIMIT %d OFFSET %d",
                name, sortField, limit, offset
        );

        return template
                .getDatabaseClient()
                .sql(sql)
                .map(this::mapRowToProductEntity)
                .all()
                .collectList();
    }

    @Override
    public Mono<List<ProductEntity>> findPaged(int offset, int limit) {
        String sql = String.format("SELECT * FROM products LIMIT %d OFFSET %d", limit, offset);

        return template
                .getDatabaseClient()
                .sql(sql)
                .map(this::mapRowToProductEntity)
                .all()
                .collectList();
    }

    @Override
    public Mono<List<ProductEntity>> findPagedSorted(int offset, int limit, String sortField) {
        String sql = String.format(
                "SELECT * FROM products ORDER BY %s LIMIT %d OFFSET %d",
                sortField, limit, offset
        );

        return template
                .getDatabaseClient()
                .sql(sql)
                .map(this::mapRowToProductEntity)
                .all()
                .collectList();
    }

    private ProductEntity mapRowToProductEntity(Readable row) {
        return new ProductEntity(
                row.get("id", Long.class),
                row.get("name", String.class),
                row.get("description", String.class),
                row.get("price", Integer.class),
                row.get("img_name", String.class)
        );
    }
}
