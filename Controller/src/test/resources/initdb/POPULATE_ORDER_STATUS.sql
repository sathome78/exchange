INSERT INTO ORDER_STATUS (id, name, description) VALUES (1, 'in process', null);
INSERT INTO ORDER_STATUS (id, name, description) VALUES (2, 'opened', null);
INSERT INTO ORDER_STATUS (id, name, description) VALUES (3, 'closed', null);
INSERT INTO ORDER_STATUS (id, name, description) VALUES (4, 'cancelled', 'cancelled by user');
INSERT INTO ORDER_STATUS (id, name, description) VALUES (5, 'deleted', 'deleted by admin');
INSERT INTO ORDER_STATUS (id, name, description) VALUES (6, 'draft', 'not submited by user');
INSERT INTO ORDER_STATUS (id, name, description) VALUES (7, 'split_closed', 'split for partial acception');