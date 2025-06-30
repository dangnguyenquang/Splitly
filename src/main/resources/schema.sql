-- ENUM types
CREATE TYPE IF NOT EXISTS payment_status AS ENUM ('WAITING', 'FAILED', 'PENDING', 'SUCCESS');
CREATE TYPE IF NOT EXISTS gender_type AS ENUM ('MALE', 'FEMALE');
CREATE TYPE IF NOT EXISTS fund_pay_status AS ENUM ('PENDING', 'SUCCESS');

-- Table: user
CREATE TABLE IF NOT EXISTS app_user (
  user_id INT PRIMARY KEY,
  username VARCHAR(50),
  phone VARCHAR(10),
  email VARCHAR(50),
  gender gender_type,
  password VARCHAR(50),
);

-- Table: group
CREATE TABLE IF NOT EXISTS group_info (
  group_id INT PRIMARY KEY,
  number_of_member INT,
  group_name VARCHAR(50)
);

-- Table: group_user
CREATE TABLE IF NOT EXISTS group_user (
  group_id INT,
  user_id INT,
  status BOOLEAN,
  joined_at TIMESTAMP,
  PRIMARY KEY (group_id, user_id),
  FOREIGN KEY (group_id) REFERENCES group_info(group_id),
  FOREIGN KEY (user_id) REFERENCES app_user(user_id)
);

-- Table: tag
CREATE TABLE IF NOT EXISTS tag (
  tag_id INT PRIMARY KEY,
  tag_name VARCHAR(50),
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  isDeleted BOOLEAN
);

-- Table: payment_request
CREATE TABLE IF NOT EXISTS payment_request (
  payment_id INT PRIMARY KEY,
  user_id INT,
  title VARCHAR(50),
  tag_id INT,
  estimated_amount DOUBLE PRECISION,
  status payment_status,
  image_url TEXT,
  payment_request_note TEXT,
  amount DOUBLE PRECISION,
  pay_list_id INT,
  used_fund_amount DOUBLE PRECISION,
  FOREIGN KEY (user_id) REFERENCES app_user(user_id),
  FOREIGN KEY (tag_id) REFERENCES tag(tag_id)
);

-- Table: items
CREATE TABLE IF NOT EXISTS items (
  item_id INT PRIMARY KEY,
  payment_id INT,
  item_name VARCHAR(50),
  amount DOUBLE PRECISION,
  FOREIGN KEY (payment_id) REFERENCES payment_request(payment_id)
);

-- Table: fund_pay
CREATE TABLE IF NOT EXISTS fund_pay (
  fund_pay_id INT PRIMARY KEY,
  status fund_pay_status,
  user_id INT,
  amount DOUBLE PRECISION,
  fund_pay_note TEXT,
  created_at TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES app_user(user_id)
);

-- Table: fund_change_type
CREATE TABLE IF NOT EXISTS fund_change_type (
  fund_change_type_id INT PRIMARY KEY,
  fund_change_type_name VARCHAR(50)
);

-- Table: fund
CREATE TABLE IF NOT EXISTS fund (
  fund_id INT PRIMARY KEY,
  new_value DOUBLE PRECISION,
  old_value DOUBLE PRECISION,
  fund_change_type_id INT,
  payment_id INT,
  fund_pay_id INT,
  group_id INT,
  fund_note TEXT,
  changed_at TIMESTAMP,
  FOREIGN KEY (fund_change_type_id) REFERENCES fund_change_type(fund_change_type_id),
  FOREIGN KEY (payment_id) REFERENCES payment_request(payment_id),
  FOREIGN KEY (fund_pay_id) REFERENCES fund_pay(fund_pay_id),
  FOREIGN KEY (group_id) REFERENCES group_info(group_id)
);

-- Table: consensus_payment
CREATE TABLE IF NOT EXISTS consensus_payment (
  user_id INT,
  payment_id INT,
  updated_at TIMESTAMP,
  created_at TIMESTAMP,
  isDeleted BOOLEAN,
  PRIMARY KEY (user_id, payment_id),
  FOREIGN KEY (user_id) REFERENCES app_user(user_id),
  FOREIGN KEY (payment_id) REFERENCES payment_request(payment_id)
);

-- Table: user_debt
CREATE TABLE IF NOT EXISTS user_debt (
  user_debt_id INT PRIMARY KEY,
  debtor_id INT,
  creditor_id INT,
  amount DOUBLE PRECISION,
  user_debt_note TEXT,
  created_at TIMESTAMP,
  status BOOLEAN,
  FOREIGN KEY (debtor_id) REFERENCES app_user(user_id),
  FOREIGN KEY (creditor_id) REFERENCES app_user(user_id)
);

-- Table: roles
CREATE TABLE IF NOT EXISTS roles (
  role_id INT PRIMARY KEY,
  role_name VARCHAR(10)
);

-- Table: permission
CREATE TABLE IF NOT EXISTS permission (
  permission_id INT PRIMARY KEY,
  permission_name VARCHAR(10)
);

-- Table: user_role
CREATE TABLE IF NOT EXISTS user_role (
  role_id INT,
  user_id INT,
  PRIMARY KEY (role_id, user_id),
  FOREIGN KEY (role_id) REFERENCES roles(role_id),
  FOREIGN KEY (user_id) REFERENCES app_user(user_id)
);

-- Table: role_permission
CREATE TABLE IF NOT EXISTS role_permission (
  permission_id INT,
  role_id INT,
  PRIMARY KEY (permission_id, role_id),
  FOREIGN KEY (permission_id) REFERENCES permission(permission_id),
  FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

-- Table: parameter
CREATE TABLE IF NOT EXISTS parameter (
  parameter_id INT,
  number_of_consensus_payment INT,
  number_of_consensus_punish INT,
  group_id INT,
  FOREIGN KEY (group_id) REFERENCES group_info(group_id)
);
