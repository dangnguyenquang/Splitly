-- app_user
INSERT INTO app_user ( username, phone, email, gender, password) VALUES
( 'alice', '0900000001', 'alice@example.com', 'FEMALE', '$2a$10$Pp0lYRW.0aVEIAO.v0xXDOiK4fFWYbqBgVP1QXDQm4mx39y3GAXei'),
('bob', '0900000002', 'bob@example.com', 'MALE', '$2a$10$SetVOAy0B7CsOuE/061EUeOD1aK6DRWzb65cB/dQJm.JBisBkNoCG'),
('carol', '0900000003', 'carol@example.com', 'FEMALE', '$2a$10$3EqxkuW2M0wVNBdsHMfU0OKAo0Oh9zSnjPPZYzkvGer8Rq/1StcXa');

-- group_info
INSERT INTO group_info ( number_of_member, group_name, leader_id) VALUES
( 3, 'Group A', 2);

-- group_user
INSERT INTO group_user (group_id, user_id, status, joined_at) VALUES
(1, 1, 'SUCCESS', '2025-06-30 04:41:44'),
(1, 2, 'SUCCESS', '2025-06-30 04:41:44'),
(1, 3, 'FAILED', '2025-06-30 04:41:44');

-- tag
INSERT INTO tag ( tag_name, created_at, updated_at, is_deleted) VALUES
( 'Food', '2025-06-30 04:41:44', '2025-06-30 04:41:44', FALSE),
( 'Travel', '2025-06-30 04:41:44', '2025-06-30 04:41:44', FALSE);

-- payment_request
INSERT INTO payment_request (user_id, title, tag_id, estimated_amount, status, image_url, payment_request_note, amount, pay_list_id, used_fund_amount) VALUES
( 1, 'Dinner', 1, 300.0, 'SUCCESS', 'url1', 'Team dinner', 310.0, 1, 100.0),
( 2, 'Trip', 2, 500.0, 'PROCESSING', 'url2', 'Weekend trip', 480.0, 2, 200.0);

-- items
INSERT INTO items ( payment_id, item_name, amount) VALUES
( 1, 'Pizza', 150.0),
( 1, 'Soda', 50.0),
( 2, 'Bus ticket', 100.0);

-- fund_change_type
INSERT INTO fund_change_type ( fund_change_type_name) VALUES
( 'Add'),
( 'Use');

-- fund_pay
INSERT INTO fund_pay ( status, user_id, amount, fund_pay_note, created_at) VALUES
( 'SUCCESS', 1, 100.0, 'Deposit', '2025-06-30 04:41:44'),
( 'PENDING', 2, 50.0, 'Deposit pending', '2025-06-30 04:41:44');

-- fund
INSERT INTO fund ( new_value, old_value, fund_change_type_id, payment_id, fund_pay_id, group_id, fund_note, changed_at) VALUES
( 1000.0, 900.0, 1, NULL, 1, 1, 'Added fund', '2025-06-30 04:41:44'),
( 900.0, 1000.0, 2, 1, NULL, 1, 'Used fund for dinner', '2025-06-30 04:41:44');
