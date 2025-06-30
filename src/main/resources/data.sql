-- app_user
INSERT INTO app_user (user_id, username, phone, email, gender, password) VALUES
(1, 'alice', '0900000001', 'alice@example.com', 'FEMALE', 'pass1'),
(2, 'bob', '0900000002', 'bob@example.com', 'MALE', 'pass2'),
(3, 'carol', '0900000003', 'carol@example.com', 'FEMALE', 'pass3');

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
