INSERT INTO location(location_id) values ('LOADING_GATE');

INSERT INTO slot_dimensions(tote_partitioning, tote_height, x, y, z) values ('BIPARTITE', 'LOW', 282, 365, 103);
INSERT INTO slot_dimensions(tote_partitioning, tote_height, x, y, z) values ('TRIPARTITE', 'LOW', 188, 365, 103);
INSERT INTO slot_dimensions(tote_partitioning, tote_height, x, y, z) values ('BIPARTITE', 'HIGH', 282, 365, 155);
INSERT INTO slot_dimensions(tote_partitioning, tote_height, x, y, z) values ('TRIPARTITE', 'HIGH', 188, 365, 155);

INSERT into authority(id, name) values (1, 'ROLE_RESUPPLY');
INSERT into authority(id, name) values (2, 'ROLE_INDUCT');
INSERT into authority(id, name) values (3, 'ROLE_PLC_SUPPORT');
INSERT into authority(id, name) values (4, 'ROLE_DEV_SUPPORT');

INSERT into role(id, name) values (1, 'DC_EMPLOYEE');
INSERT into role(id, name) values (2, 'RCS_EMPLOYEE');
INSERT into role(id, name) values (3, 'PLC_SUPPORT');
INSERT into role(id, name) values (4, 'SUPERUSER');
INSERT into role(id, name) values (5, 'DEVELOPER');

INSERT into role_authorities(role_id, authorities_id) values (1, 1);
INSERT into role_authorities(role_id, authorities_id) values (2, 1);
INSERT into role_authorities(role_id, authorities_id) values (2, 2);
INSERT into role_authorities(role_id, authorities_id) values (3, 3);
INSERT into role_authorities(role_id, authorities_id) values (4, 1);
INSERT into role_authorities(role_id, authorities_id) values (4, 2);
INSERT into role_authorities(role_id, authorities_id) values (4, 3);
INSERT into role_authorities(role_id, authorities_id) values (4, 4);
INSERT into role_authorities(role_id, authorities_id) values (5, 4);

INSERT into users(id, username, password, enabled, firstname, lastname) VALUES (8, 'kannart', '$2a$10$q6qZD7lCj6ssWQmQHvwpV.82x.g80lWjYUp6hB26wEJJgl55DKlj.', true, 'Commerce', 'Platform');
INSERT into users(id, username, password, enabled, firstname, lastname) VALUES (9, 'mhe', '$2a$10$q6qZD7lCj6ssWQmQHvwpV.82x.g80lWjYUp6hB26wEJJgl55DKlj.', true, 'Machine', 'Operator');

insert into users_roles(users_id, roles_id) values (8, 4);
insert into users_roles(users_id, roles_id) values (9, 4);