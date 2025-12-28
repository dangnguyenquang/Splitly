-- app_user
INSERT INTO
        app_user (
                username,
                phone,
                email,
                gender,
                password,
                user_image,
                is_verified,
                reset_token,
                reset_token_expiry
        )
VALUES
        (
                'alice',
                '0900000001',
                'alice@example.com',
                'FEMALE',
                '$2a$10$Pp0lYRW.0aVEIAO.v0xXDOiK4fFWYbqBgVP1QXDQm4mx39y3GAXei',
                'https://res.cloudinary.com/diyxertpi/image/upload/v1766500889/default-avatar-avif_ujmbe7.avif',
                true,
                null,
                null
        ),
        (
                'bob',
                '0900000002',
                'bob@example.com',
                'MALE',
                '$2a$10$SetVOAy0B7CsOuE/061EUeOD1aK6DRWzb65cB/dQJm.JBisBkNoCG',
                'https://res.cloudinary.com/diyxertpi/image/upload/v1766500889/default-avatar-avif_ujmbe7.avif',
                true,
                null,
                null
        ),
        (
                'carol',
                '0900000003',
                'carol@example.com',
                'FEMALE',
                '$2a$10$3EqxkuW2M0wVNBdsHMfU0OKAo0Oh9zSnjPPZYzkvGer8Rq/1StcXa',
                'https://res.cloudinary.com/diyxertpi/image/upload/v1766500889/default-avatar-avif_ujmbe7.avif',
                true,
                null,
                null
        ),
        (
                'dave',
                '0900000004',
                'dave@example.com',
                'MALE',
                '$2a$10$Pp0lYRW.0aVEIAO.v0xXDOiK4fFWYbqBgVP1QXDQm4mx39y3GAXei',
                'https://res.cloudinary.com/diyxertpi/image/upload/v1766500889/default-avatar-avif_ujmbe7.avif',
                true,
                null,
                null
        ),
        (
                'emma',
                '0900000005',
                'emma@example.com',
                'FEMALE',
                '$2a$10$Pp0lYRW.0aVEIAO.v0xXDOiK4fFWYbqBgVP1QXDQm4mx39y3GAXei',
                'https://res.cloudinary.com/diyxertpi/image/upload/v1766500889/default-avatar-avif_ujmbe7.avif',
                true,
                null,
                null
        ),
        (
                'frank',
                '0900000006',
                'frank@example.com',
                'MALE',
                '$2a$10$Pp0lYRW.0aVEIAO.v0xXDOiK4fFWYbqBgVP1QXDQm4mx39y3GAXei',
                'https://res.cloudinary.com/diyxertpi/image/upload/v1766500889/default-avatar-avif_ujmbe7.avif',
                true,
                null,
                null
        ),
        (
                'grace',
                '0900000007',
                'grace@example.com',
                'FEMALE',
                '$2a$10$Pp0lYRW.0aVEIAO.v0xXDOiK4fFWYbqBgVP1QXDQm4mx39y3GAXei',
                'https://res.cloudinary.com/diyxertpi/image/upload/v1766500889/default-avatar-avif_ujmbe7.avif',
                true,
                null,
                null
        ),
        (
                'henry',
                '0900000008',
                'henry@example.com',
                'MALE',
                '$2a$10$Pp0lYRW.0aVEIAO.v0xXDOiK4fFWYbqBgVP1QXDQm4mx39y3GAXei',
                'https://res.cloudinary.com/diyxertpi/image/upload/v1766500889/default-avatar-avif_ujmbe7.avif',
                true,
                null,
                null
        ),
        (
                'ivy',
                '0900000009',
                'ivy@example.com',
                'FEMALE',
                '$2a$10$Pp0lYRW.0aVEIAO.v0xXDOiK4fFWYbqBgVP1QXDQm4mx39y3GAXei',
                'https://res.cloudinary.com/diyxertpi/image/upload/v1766500889/default-avatar-avif_ujmbe7.avif',
                true,
                null,
                null
        ),
        (
                'jack',
                '0900000010',
                'jack@example.com',
                'MALE',
                '$2a$10$Pp0lYRW.0aVEIAO.v0xXDOiK4fFWYbqBgVP1QXDQm4mx39y3GAXei',
                'https://res.cloudinary.com/diyxertpi/image/upload/v1766500889/default-avatar-avif_ujmbe7.avif',
                true,
                null,
                null
        );

-- user_connection
INSERT INTO
        user_connection (
                request_user_id,
                receive_user_id,
                is_accepted
        )
VALUES
        (1, 2, true),
        (2, 3, false),
        (3, 1, false),
        (4, 1, true),
        (5, 1, false),
        (6, 2, true),
        (7, 3, false),
        (8, 2, true),
        (9, 4, false),
        (10, 5, true);

-- group_info
INSERT INTO
        group_info (
                number_of_member,
                group_name,
                group_image,
                image_public_id,
                leader_id,
                descriptions,
                currency,
                category,
                created_at,
                updated_at
        )
VALUES
        (
                3,
                'Group A',
                NULL,
                NULL,
                2,
                'Use for fee',
                'VND',
                'SOCIAL',
                '2025-06-30 04:41:44',
                '2025-06-30 04:45:44'
        ),
        (
                4,
                'Group B',
                'https://res.cloudinary.com/diyxertpi/image/upload/v1766500889/default-avatar-avif_ujmbe7.avif',
                NULL,
                3,
                'Friends hangout group',
                'VND',
                'SOCIAL',
                '2025-07-01 09:00:00',
                '2025-07-01 09:00:00'
        ),
        (
                3,
                'Group C',
                'https://res.cloudinary.com/diyxertpi/image/upload/v1766500889/default-avatar-avif_ujmbe7.avif',
                NULL,
                4,
                'Office expenses',
                'VND',
                'WORK',
                '2025-07-01 09:05:00',
                '2025-07-01 09:05:00'
        ),
        (
                5,
                'Group D',
                'https://res.cloudinary.com/diyxertpi/image/upload/v1766500889/default-avatar-avif_ujmbe7.avif',
                NULL,
                6,
                'Travel buddies',
                'VND',
                'TRAVEL',
                '2025-07-01 09:10:00',
                '2025-07-01 09:10:00'
        );

-- group_user
INSERT INTO
        group_user (group_id, user_id, status, joined_at)
VALUES
        (1, 1, 'SUCCESS', '2025-06-30 04:41:44'),
        (1, 2, 'SUCCESS', '2025-06-30 04:41:44'),
        (1, 3, 'FAILED', '2025-06-30 04:41:44');

-- tag
INSERT INTO
        tag (tag_name, created_at, updated_at, is_deleted)
VALUES
        (
                'Food',
                '2025-06-30 04:41:44',
                '2025-06-30 04:41:44',
                FALSE
        ),
        (
                'Travel',
                '2025-06-30 04:41:44',
                '2025-06-30 04:41:44',
                FALSE
        ),
        (
                'Music',
                '2025-06-30 04:41:44',
                '2025-06-30 04:41:44',
                FALSE
        ),
        (
                'Movie',
                '2025-06-30 04:41:44',
                '2025-06-30 04:41:44',
                FALSE
        ),
        (
                'Sport',
                '2025-06-30 04:41:44',
                '2025-06-30 04:41:44',
                FALSE
        ),
        (
                'Games',
                '2025-06-30 04:41:44',
                '2025-06-30 04:41:44',
                FALSE
        ),
        (
                'Dining out',
                '2025-06-30 04:41:44',
                '2025-06-30 04:41:44',
                FALSE
        ),
        (
                'Liquor',
                '2025-06-30 04:41:44',
                '2025-06-30 04:41:44',
                FALSE
        ),
        (
                'Market',
                '2025-06-30 04:41:44',
                '2025-06-30 04:41:44',
                FALSE
        ),
        (
                'Utilities',
                '2025-06-30 04:41:44',
                '2025-06-30 04:41:44',
                FALSE
        );

-- payment_request
INSERT INTO
        payment_request (
                payment_id,
                user_id,
                title,
                tag_id,
                estimated_amount,
                status,
                payment_request_note,
                amount,
                used_fund_amount,
                group_id
        )
VALUES
        (
                1,
                1,
                'Dinner',
                1,
                300.0,
                'SUCCESS',
                'Team dinner',
                310.0,
                100.0,
                1
        ),
        (
                2,
                2,
                'Trip',
                2,
                500.0,
                'PROCESSING',
                'Weekend trip',
                480.0,
                200.0,
                1
        ),
        (
                3,
                4,
                'Lunch',
                1,
                200,
                'SUCCESS',
                'Office lunch',
                190,
                50,
                1
        ),
        (
                4,
                5,
                'Movie night',
                4,
                180,
                'WAITING',
                'Cinema',
                0,
                0,
                1
        ),
        (
                5,
                6,
                'Groceries',
                9,
                400,
                'PROCESSING',
                'Weekly market',
                380,
                100,
                1
        ),
        (
                6,
                7,
                'Beer',
                8,
                250,
                'SUCCESS',
                'Friday night',
                260,
                120,
                1
        );

-- items
INSERT INTO
        items (
                item_id,
                payment_id,
                item_name,
                amount,
                quantity,
                price_quotation
        )
VALUES
        (1, 1, 'Pizza', 150.0, 1, 150.0),
        (2, 1, 'Soda', 50.0, 2, 25.0),
        (3, 2, 'Bus ticket', 100.0, 2, 50.0),
        (4, 3, 'Rice set', 120, 2, 60),
        (5, 3, 'Juice', 70, 2, 35),
        (6, 4, 'Movie ticket', 90, 2, 45),
        (7, 5, 'Vegetables', 200, 1, 200),
        (8, 5, 'Meat', 180, 1, 180),
        (9, 6, 'Beer pack', 260, 1, 260);

-- fund_change_type
INSERT INTO
        fund_change_type (fund_change_type_id, fund_change_type_name)
VALUES
        (1, 'Add'),
        (2, 'Use');

-- fund_pay
INSERT INTO
        fund_pay (
                status,
                user_id,
                amount,
                fund_pay_note,
                created_at
        )
VALUES
        (
                'SUCCESS',
                1,
                100.0,
                'Deposit',
                '2025-06-30 04:41:44'
        ),
        (
                'PENDING',
                2,
                50.0,
                'Deposit pending',
                '2025-06-30 04:41:44'
        ),
        (
                'SUCCESS',
                3,
                200,
                'Top up',
                '2025-07-01 10:00:00'
        ),
        (
                'SUCCESS',
                4,
                300,
                'Top up',
                '2025-07-01 10:05:00'
        ),
        (
                'PENDING',
                5,
                150,
                'Top up pending',
                '2025-07-01 10:10:00'
        );

-- fund
INSERT INTO
        fund (
                new_value,
                old_value,
                fund_change_type_id,
                payment_id,
                fund_pay_id,
                group_id,
                fund_note,
                changed_at
        )
VALUES
        (
                1000.0,
                900.0,
                1,
                NULL,
                1,
                1,
                'Added fund',
                '2025-06-30 04:41:44'
        ),
        (
                900.0,
                1000.0,
                2,
                1,
                NULL,
                1,
                'Used fund for dinner',
                '2025-06-30 04:41:44'
        );

-- consensus_payment
INSERT INTO
        consensus_payment (
                user_id,
                payment_id,
                updated_at,
                created_at,
                is_process_accepted,
                is_success_accepted
        )
VALUES
        (
                1,
                1,
                '2025-06-30 04:41:44',
                '2025-06-30 04:41:44',
                FALSE,
                FALSE
        ),
        (
                2,
                1,
                '2025-06-30 04:41:44',
                '2025-06-30 04:41:44',
                FALSE,
                FALSE
        ),
        (
                3,
                1,
                '2025-06-30 04:41:44',
                '2025-06-30 04:41:44',
                TRUE,
                FALSE
        ),
        (4, 3, NOW(), NOW(), true, true),
        (5, 3, NOW(), NOW(), true, true),
        (6, 4, NOW(), NOW(), false, false),
        (7, 5, NOW(), NOW(), true, false),
        (8, 6, NOW(), NOW(), true, true);

-- user_debt
INSERT INTO
        user_debt (
                user_debt_id,
                debtor_id,
                creditor_id,
                amount,
                user_debt_note,
                created_at,
                status,
                group_id,
                payment_id
        )
VALUES
        (
                1,
                2,
                1,
                150.0,
                'Owes for dinner',
                '2025-06-30 04:41:44',
                TRUE,
                1,
                1
        ),
        (
                2,
                3,
                1,
                160.0,
                'Owes for dinner',
                '2025-06-30 04:41:44',
                FALSE,
                1,
                1
        ),
        (3, 4, 1, 95, 'Lunch split', NOW(), true, 1, 3),
        (4, 5, 4, 90, 'Movie share', NOW(), false, 1, 4),
        (5, 6, 1, 190, 'Groceries', NOW(), false, 1, 5),
        (6, 7, 2, 130, 'Beer split', NOW(), true, 1, 6);

-- roles
INSERT INTO
        roles (role_id, role_name)
VALUES
        (1, 'ADMIN'),
        (2, 'MEMBER');

-- permission
INSERT INTO
        permission (permission_id, permission_name)
VALUES
        (1, 'READ'),
        (2, 'WRITE');

-- user_role
INSERT INTO
        user_role (role_id, user_id)
VALUES
        (1, 1),
        (2, 2),
        (2, 3);

-- role_permission
INSERT INTO
        role_permission (permission_id, role_id)
VALUES
        (1, 1),
        (2, 1),
        (1, 2);

-- parameter
INSERT INTO
        parameter (
                parameter_id,
                number_of_consensus_payment,
                number_of_consensus_punish,
                group_id
        )
VALUES
        (1, 2, 1, 1);