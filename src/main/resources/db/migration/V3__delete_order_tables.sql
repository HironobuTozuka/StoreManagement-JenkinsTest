alter table customer_order_transaction
    drop constraint fk_order_id_customer_order_id;
alter table delivery_inventory
    drop constraint fk_slot_order_id_customer_order_id;

alter table customer_order_transaction
    alter column order_id TYPE varchar(255);

alter table customer_order_transaction
    add column created_at timestamp;