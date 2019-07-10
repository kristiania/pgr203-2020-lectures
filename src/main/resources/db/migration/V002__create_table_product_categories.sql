create table product_categories (
    id serial primary key,
    name varchar(100) not null
);

alter table products add product_category integer not null references product_categories(id);
