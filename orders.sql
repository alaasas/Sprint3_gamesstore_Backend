CREATE TABLE game.orders (
    order_id VARCHAR(200),
    user_id VARCHAR(200),
    gamename VARCHAR(2000),
    fees Double,
    PRIMARY KEY(order_id)
);