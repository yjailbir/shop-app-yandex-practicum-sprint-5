CREATE TABLE products (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          name VARCHAR(255) NOT NULL,
                          description VARCHAR(1000),
                          price INT,
                          img_name VARCHAR(255)
);

CREATE TABLE orders (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT
    -- остальные поля пока не указаны, если будут, дополни здесь
);

CREATE TABLE order_items (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             order_id BIGINT,
                             product_id BIGINT,
                             quantity INT,
                             CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                             CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE cart (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      product_id BIGINT,
                      quantity INT,
                      CONSTRAINT fk_cart_product FOREIGN KEY (product_id) REFERENCES products(id)
);