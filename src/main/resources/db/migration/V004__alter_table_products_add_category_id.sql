alter table products add column category_id integer null references product_categories(id);
