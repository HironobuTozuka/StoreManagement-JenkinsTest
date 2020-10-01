CREATE TABLE sku
(
    sku_id varchar(255) NOT NULL,
    external_id varchar(255) UNIQUE NOT NULL,
    x int,
    y int,
    z int,
    weight double precision,
    max_acc double precision,
    image_url varchar(255),
    name varchar(255),
    status varchar(20),
    category varchar(255),
    mujin_name varchar(50),
    distribution_type varchar(20),
    CONSTRAINT sku_pkey PRIMARY KEY (sku_id)
);

CREATE TABLE customer_order
(
    id SERIAL NOT NULL,
    order_id varchar(255) NOT NULL UNIQUE,
    order_Type varchar(255) NOT NULL,
    pickup_time timestamp,
    gate_id varchar(255),
    user_id varchar(255),
    order_status varchar(255) NOT NULL,
    version integer NOT NULL,
    created_at timestamp,
    delivered_at timestamp,
    collected_at timestamp,
    CONSTRAINT order_pkey PRIMARY KEY (id)
);

CREATE TABLE sku_batch
(
    id SERIAL NOT NULL,
    sku_id varchar(255),
    quantity integer,
    sell_by_date timestamp,
    state varchar(255),
    CONSTRAINT sku_batch_pkey PRIMARY KEY (id),
    CONSTRAINT fk_sku_batch_sku_id_sku_id FOREIGN KEY (sku_id) REFERENCES sku (sku_id)
);

CREATE TABLE storage_inventory
(
    id SERIAL NOT NULL,
    sku_batch_id integer,
    available integer,
    CONSTRAINT storage_inventory_pkey PRIMARY KEY (id),
    CONSTRAINT fk_storage_inventory_sku_batch_id_sku_batch_id FOREIGN KEY (sku_batch_id) REFERENCES sku_batch (id)
);

CREATE TABLE delivery_inventory
(
    id SERIAL NOT NULL,
    order_id varchar(255),
    CONSTRAINT delivery_inventory_pkey PRIMARY KEY (id),
    CONSTRAINT fk_slot_order_id_customer_order_id FOREIGN KEY (order_id) REFERENCES customer_order (order_id)
);

CREATE TABLE tote
(
    id SERIAL NOT NULL,
    tote_id varchar(255),
    tote_height varchar(255),
    tote_orientation varchar(255),
    tote_partitioning varchar(255),
    tote_status varchar(255),
    zone_id varchar(50),
    temperature_regime varchar(20),
    tote_function varchar(255),
    CONSTRAINT tote_pkey PRIMARY KEY (id)
);

CREATE TABLE slot
(
    id SERIAL NOT NULL,
    ordinal integer NOT NULL,
    storage_inventory_id integer,
    delivery_inventory_id integer,
    tote_id integer,
    CONSTRAINT slot_pkey PRIMARY KEY (id),
    CONSTRAINT fk_slot_delivery_inventory_id_delivery_inventory_id FOREIGN KEY (delivery_inventory_id) REFERENCES delivery_inventory (id),
    CONSTRAINT fk_slot_storage_inventory_id_storage_inventory_id FOREIGN KEY (storage_inventory_id) REFERENCES storage_inventory (id),
    CONSTRAINT fk_slot_tote_id_tote_id FOREIGN KEY (tote_id) REFERENCES tote (id),
    CONSTRAINT unique_tote_id_ordinal UNIQUE (ordinal, tote_id)
);

CREATE TABLE delivery_inventory_sku_batches
(
    delivery_inventory_id integer,
    sku_batches_id integer,
    CONSTRAINT fk_delivery_inventory_sku_batches_sku_batches_id FOREIGN KEY (sku_batches_id) REFERENCES sku_batch (id),
    CONSTRAINT fk_delivery_inventory_sku_batches_delivery_inventory_id FOREIGN KEY (delivery_inventory_id) REFERENCES delivery_inventory (id)
);

CREATE TABLE location
(
    id SERIAL NOT NULL,
    location_id varchar(255),
    current_tote_id int,
    CONSTRAINT location_pkey PRIMARY KEY (id),
    CONSTRAINT unique_location_id UNIQUE (location_id),
    CONSTRAINT fk_location_tote_id_tote_id FOREIGN KEY (current_tote_id)
        REFERENCES tote (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE slot_dimensions
(
    tote_partitioning varchar(255) NOT NULL,
    tote_height varchar(255) NOT NULL,
    x int,
    y int,
    z int,
    CONSTRAINT slot_dimensions_pkey PRIMARY KEY (tote_partitioning, tote_height)
);

CREATE TABLE order_line
(
    id SERIAL NOT NULL,
    order_line_id varchar(255) NOT NULL,
    sku_id varchar(255),
    quantity int,
    picked int,
    failed int,
    customer_order_id int,
    CONSTRAINT order_line_pkey PRIMARY KEY (id),
    CONSTRAINT fk_order_line_sku_id FOREIGN KEY (sku_id) REFERENCES sku (sku_id),
    CONSTRAINT fk_customer_order_order_lines_order_id FOREIGN KEY (customer_order_id) REFERENCES customer_order (id)
);

CREATE TABLE reservation
(
    id SERIAL NOT NULL,
    order_line_id integer,
    quantity int NOT NULL,
    sku_id varchar(255),
    delivery_slot_id integer,
    CONSTRAINT reservation_pkey PRIMARY KEY (id),
    CONSTRAINT fk_reservation_order_line_id FOREIGN KEY (order_line_id) REFERENCES order_line (id)
);

CREATE TABLE storage_inventory_reservations
(
    storage_inventory_id int,
    reservations_id int,
    CONSTRAINT fk_storage_inventory_reservations_storage_inventory_id FOREIGN KEY (storage_inventory_id) REFERENCES storage_inventory (id),
    CONSTRAINT fk_storage_inventory_reservations_reservations_id FOREIGN KEY (reservations_id) REFERENCES reservation (id),
    CONSTRAINT unique_storage_reservations_id UNIQUE(reservations_id)
);

CREATE TABLE delivery_inventory_reservations
(
    delivery_inventory_id int,
    reservations_id int,
    CONSTRAINT fk_delivery_inventory_reservations_delivery_inventory_id FOREIGN KEY (delivery_inventory_id) REFERENCES delivery_inventory (id),
    CONSTRAINT fk_delivery_inventory_reservations_reservations_id FOREIGN KEY (reservations_id) REFERENCES reservation (id),
    CONSTRAINT unique_delivery_reservations_id UNIQUE(reservations_id)
);

CREATE TABLE task
(
    id SERIAL NOT NULL,
    dtype varchar(255) NOT NULL,
    destination_slot_ordinal integer,
    destination_tote_id varchar(255),
    sku_id varchar(255),
    source_slot_ordinal integer,
    source_tote_id varchar(255),
    quantity integer,
    reservation_id integer,
    task_id varchar(255) NOT NULL,
    status varchar(255) NOT NULL,
    destination varchar(255),
    tote_id varchar(255),
    order_id varchar(255),
    fail_reason varchar(255),
    CONSTRAINT task_pkey PRIMARY KEY (id),
    CONSTRAINT fk_task_reservation_id_reservation_id FOREIGN KEY (reservation_id) REFERENCES reservation (id),
    CONSTRAINT fk_task_order_id_customer_order_order_id FOREIGN KEY (order_id) REFERENCES customer_order (order_id)
);

CREATE TABLE task_bundle
(
    id SERIAL NOT NULL,
    status varchar(255) NOT NULL,
    order_id varchar(255),
    type varchar(255),
    task_bundle_id varchar(255),
    CONSTRAINT task_bundle_pkey PRIMARY KEY (id),
    CONSTRAINT fk_task_bundle_order_id_customer_order_id FOREIGN KEY (order_id) REFERENCES customer_order (order_id)
);

CREATE TABLE task_bundle_tasks
(
    task_bundle_id integer NOT NULL,
    tasks_id integer NOT NULL,
    CONSTRAINT fk_task_bundle_tasks_task_bundle_id FOREIGN KEY (task_bundle_id) REFERENCES task_bundle (id),
    CONSTRAINT fk_task_bundle_tasks_tasks_id FOREIGN KEY (tasks_id) REFERENCES task (id)
);

CREATE TABLE users
(
    id SERIAL NOT NULL,
    username varchar(255) UNIQUE NOT NULL,
    password varchar(255),
    enabled boolean,
    firstname varchar(255) NOT NULL,
    lastname varchar(255) NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id)
);

CREATE TABLE role
(
    id SERIAL NOT NULL,
    name varchar(255) UNIQUE NOT NULL,
    CONSTRAINT role_pkey PRIMARY KEY (id)
);

CREATE TABLE authority
(
    id SERIAL NOT NULL,
    name varchar(255) UNIQUE NOT NULL,
    CONSTRAINT authority_pkey PRIMARY KEY (id)
);

CREATE TABLE role_authorities
(
    role_id int NOT NULL,
    authorities_id int NOT NULL,
    CONSTRAINT fk_role_authorities_role_id_role_id FOREIGN KEY (role_id) references role(id),
    CONSTRAINT fk_role_authorities_authorities_id_authority_id FOREIGN KEY (authorities_id) references authority(id)
);

CREATE TABLE users_roles
(
    users_id int NOT NULL,
    roles_id int NOT NULL,
    CONSTRAINT fk_role_authorities_user_id_user_id FOREIGN KEY (users_id) references users(id),
    CONSTRAINT fk_role_authorities_roles_id_role_id FOREIGN KEY (roles_id) references role(id)
);

create table scheduled_supply
(
    id SERIAL NOT NULL primary key,
    supply_id varchar(255),
    distribution_type varchar(255),
    delivery_date date,
    delivery_turn varchar(255)
);

create table scheduled_supply_item
(
    id SERIAL NOT NULL primary key,
    scheduled_supply_item_id varchar(255),
    sku_id varchar(255),
    quantity int,
    inducted_quantity int,
    sell_by_date timestamp,
    scheduled_supply_id int,
    CONSTRAINT fk_scheduled_supply_item_scheduled_supply_id_scheduled_supply_id FOREIGN KEY (scheduled_supply_id) references scheduled_supply (id)
);

create table customer_order_transaction
(
    id SERIAL NOT NULL,
    transaction_type varchar(255),
    order_id int,
    transaction_id varchar(255),
    CONSTRAINT order_transaction_pk PRIMARY KEY (id),
    CONSTRAINT fk_order_id_customer_order_id FOREIGN KEY (order_id) REFERENCES customer_order (id)
);

CREATE TABLE filter (
    filter_id VARCHAR(255) UNIQUE,
    target VARCHAR(255),
    fields VARCHAR
);

ALTER TABLE filter ADD CONSTRAINT filter_pkey PRIMARY KEY (filter_id);

create table issue(
    id SERIAL NOT NULL,
    created timestamp,
    version int,
    issue_id varchar(255) NOT NULL,
    issue_deadline timestamp,
    issue_action varchar(255) NOT NULL,
    issue_status varchar(255) NOT NULL,
    reason varchar(255) NOT NULL,
    tote_id varchar(255),
    notes varchar(255),
    sku_id varchar(255),
    temperature_regime varchar(220),
    order_id varchar(255),
    CONSTRAINT issue_pkey PRIMARY KEY (id)
);

create table configuration(
    key varchar(255) PRIMARY KEY,
    value varchar(255)
);

