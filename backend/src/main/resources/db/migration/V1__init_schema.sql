CREATE TABLE product
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL,
    description TEXT,
    price       NUMERIC(10, 2) NOT NULL,
    created_at  TIMESTAMP      NULL,
    updated_at  TIMESTAMP      NULL
);

CREATE INDEX idx_product_name_lower ON product (LOWER(name));

CREATE TABLE inventory
(
    product_id UUID PRIMARY KEY,
    quantity   INTEGER   NOT NULL CHECK (quantity >= 0),
    version    BIGINT,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    CONSTRAINT fk_inventory_product
        FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE CASCADE
);

CREATE TABLE customer
(
    id            UUID PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    mobile_number VARCHAR(32)  NOT NULL,
    email         VARCHAR(255),
    created_at    TIMESTAMP    NULL,
    updated_at    TIMESTAMP    NULL
);

CREATE UNIQUE INDEX ux_customer_mobile_number ON customer (mobile_number);

CREATE TABLE orders
(
    id           UUID PRIMARY KEY,
    customer_id  UUID           NULL,
    total_amount NUMERIC(12, 2) NOT NULL DEFAULT 0,
    status       VARCHAR(32)    NOT NULL,
    created_at   TIMESTAMP      NULL,
    updated_at   TIMESTAMP      NULL,
    CONSTRAINT fk_orders_customer
        FOREIGN KEY (customer_id) REFERENCES customer (id)
);

CREATE INDEX idx_orders_created_at ON orders (created_at);

CREATE TABLE order_item
(
    id         UUID PRIMARY KEY,
    order_id   UUID           NOT NULL,
    product_id UUID           NOT NULL,
    quantity   INTEGER        NOT NULL CHECK (quantity > 0),
    sale_price NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP      NULL,
    updated_at TIMESTAMP      NULL,
    CONSTRAINT fk_order_item_order
        FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_order_item_product
        FOREIGN KEY (product_id) REFERENCES product (id)
);

CREATE INDEX idx_order_item_order_id ON order_item (order_id);
CREATE INDEX idx_order_item_product_id ON order_item (product_id);