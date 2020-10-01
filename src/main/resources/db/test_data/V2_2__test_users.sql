INSERT into users(id, username, password, enabled, firstname, lastname) VALUES (1, 'mszpak', '$2a$10$q6qZD7lCj6ssWQmQHvwpV.82x.g80lWjYUp6hB26wEJJgl55DKlj.', true, 'Mateusz', 'Szpak');
INSERT into users(id, username, password, enabled, firstname, lastname) VALUES (2, 'kfrejlich', '$2a$10$q6qZD7lCj6ssWQmQHvwpV.82x.g80lWjYUp6hB26wEJJgl55DKlj.', true, 'Krzysztof', 'Frejlich');
INSERT into users(id, username, password, enabled, firstname, lastname) VALUES (3, 'dc', '$2a$10$q6qZD7lCj6ssWQmQHvwpV.82x.g80lWjYUp6hB26wEJJgl55DKlj.', true, 'Jan', 'DC Employee');
INSERT into users(id, username, password, enabled, firstname, lastname) VALUES (4, 'dev', '$2a$10$q6qZD7lCj6ssWQmQHvwpV.82x.g80lWjYUp6hB26wEJJgl55DKlj.', true, 'Jerzy', 'Developer');
INSERT into users(id, username, password, enabled, firstname, lastname) VALUES (5, 'plc', '$2a$10$q6qZD7lCj6ssWQmQHvwpV.82x.g80lWjYUp6hB26wEJJgl55DKlj.', true, 'Franciszek', 'PLC Support');
INSERT into users(id, username, password, enabled, firstname, lastname) VALUES (6, 'rcs', '$2a$10$q6qZD7lCj6ssWQmQHvwpV.82x.g80lWjYUp6hB26wEJJgl55DKlj.', true, 'Hubert', 'Rcs Employee');
INSERT into users(id, username, password, enabled, firstname, lastname) VALUES (7, 'cnowak', '$2a$10$q6qZD7lCj6ssWQmQHvwpV.82x.g80lWjYUp6hB26wEJJgl55DKlj.', true, 'Cezary', 'Nowak');

insert into users_roles(users_id, roles_id) values (1, 4);
insert into users_roles(users_id, roles_id) values (2, 4);
insert into users_roles(users_id, roles_id) values (7, 4);
insert into users_roles(users_id, roles_id) values (3, 1);
insert into users_roles(users_id, roles_id) values (4, 5);
insert into users_roles(users_id, roles_id) values (5, 3);
insert into users_roles(users_id, roles_id) values (6, 2);