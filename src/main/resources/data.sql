-- app_user
INSERT INTO app_user (user_id, username, phone, email, gender, password) VALUES
(1, 'alice', '0900000001', 'alice@example.com', 'FEMALE', '$2a$10$Pp0lYRW.0aVEIAO.v0xXDOiK4fFWYbqBgVP1QXDQm4mx39y3GAXei'),
(2, 'bob', '0900000002', 'bob@example.com', 'MALE', '$2a$10$SetVOAy0B7CsOuE/061EUeOD1aK6DRWzb65cB/dQJm.JBisBkNoCG'),
(3, 'carol', '0900000003', 'carol@example.com', 'FEMALE', '$2a$10$3EqxkuW2M0wVNBdsHMfU0OKAo0Oh9zSnjPPZYzkvGer8Rq/1StcXa');

-- group_info
INSERT INTO group_info (group_id, number_of_member, group_name) VALUES
(1, 3, 'Group A');

-- group_user
INSERT INTO group_user (group_id, user_id, status, joined_at) VALUES
(1, 1, TRUE, '2025-06-30 04:41:44'),
(1, 2, TRUE, '2025-06-30 04:41:44'),
(1, 3, FALSE, '2025-06-30 04:41:44');

-- tag
INSERT INTO tag (tag_id, tag_name, created_at, updated_at, isDeleted) VALUES
(1, 'Food', '2025-06-30 04:41:44', '2025-06-30 04:41:44', FALSE),
(2, 'Travel', '2025-06-30 04:41:44', '2025-06-30 04:41:44', FALSE);

-- payment_request
INSERT INTO payment_request (payment_id, user_id, title, tag_id, estimated_amount, status, image_url, payment_request_note, amount, pay_list_id, used_fund_amount) VALUES
(1, 1, 'Dinner', 1, 300.0, 'SUCCESS', 'url1', 'Team dinner', 310.0, 1, 100.0),
(2, 2, 'Trip', 2, 500.0, 'PENDING', 'url2', 'Weekend trip', 480.0, 2, 200.0);

-- items
INSERT INTO items (item_id, payment_id, item_name, amount) VALUES
(1, 1, 'Pizza', 150.0),
(2, 1, 'Soda', 50.0),
(3, 2, 'Bus ticket', 100.0);

-- fund_change_type
INSERT INTO fund_change_type (fund_change_type_id, fund_change_type_name) VALUES
(1, 'Add'),
(2, 'Use');

-- fund_pay
INSERT INTO fund_pay (fund_pay_id, status, user_id, amount, fund_pay_note, created_at) VALUES
(1, 'SUCCESS', 1, 100.0, 'Deposit', '2025-06-30 04:41:44'),
(2, 'PENDING', 2, 50.0, 'Deposit pending', '2025-06-30 04:41:44');

-- fund
INSERT INTO fund (fund_id, new_value, old_value, fund_change_type_id, payment_id, fund_pay_id, group_id, fund_note, changed_at) VALUES
(1, 1000.0, 900.0, 1, NULL, 1, 1, 'Added fund', '2025-06-30 04:41:44'),
(2, 900.0, 1000.0, 2, 1, NULL, 1, 'Used fund for dinner', '2025-06-30 04:41:44');

-- consensus_payment
INSERT INTO consensus_payment (user_id, payment_id, updated_at, created_at, isDeleted) VALUES
(1, 1, '2025-06-30 04:41:44', '2025-06-30 04:41:44', FALSE),
(2, 1, '2025-06-30 04:41:44', '2025-06-30 04:41:44', FALSE),
(3, 1, '2025-06-30 04:41:44', '2025-06-30 04:41:44', TRUE);

-- user_debt
INSERT INTO user_debt (user_debt_id, debtor_id, creditor_id, amount, user_debt_note, created_at, status) VALUES
(1, 2, 1, 150.0, 'Owes for dinner', '2025-06-30 04:41:44', TRUE),
(2, 3, 1, 160.0, 'Owes for dinner', '2025-06-30 04:41:44', FALSE);

-- roles
INSERT INTO roles (role_id, role_name) VALUES
(1, 'ADMIN'),
(2, 'MEMBER');

-- permission
INSERT INTO permission (permission_id, permission_name) VALUES
(1, 'READ'),
(2, 'WRITE');


-- user_role
INSERT INTO user_role (role_id, user_id) VALUES
(1, 1),  
(2, 2),  
(2, 3);  


-- role_permission
INSERT INTO role_permission (permission_id, role_id) VALUES
(1, 1),  
(2, 1),  
(1, 2);  

-- parameter
INSERT INTO parameter (parameter_id, number_of_consensus_payment, number_of_consensus_punish, group_id) VALUES
(1, 2, 1, 1);

