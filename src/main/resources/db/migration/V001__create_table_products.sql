create table products
(
    id serial not null
        constraint products_pkey
            primary key,
    product_name varchar(100) not null,
    price numeric
);
