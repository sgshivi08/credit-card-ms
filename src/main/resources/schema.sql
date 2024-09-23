CREATE TABLE credit_card_application (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    emirates_id_number VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    mobile_number VARCHAR(20) NOT NULL,
    nationality VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    income DECIMAL(15, 2) NOT NULL,
    employment_details VARCHAR(255),
    requested_credit_limit DECIMAL(15, 2),
    bank_statement_path VARCHAR(255), -- Store the file path
    score INT
);