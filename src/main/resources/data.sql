INSERT INTO credit_card_application (
    emirates_id_number, name, mobile_number, nationality, address, income, employment_details, requested_credit_limit, bank_statement_path,score
) VALUES
('784-1984-1234567-0', 'John Doe', '+971501234567', 'UAE', '1234 Elm St, Dubai, UAE', 120000.00, 'Software Engineer at XYZ Corp', 50000.00, 'classpath:bank_statements/bank_statement_john_doe.pdf',100),
('784-1986-7654321-1', 'Jane Smith', '+971509876543', 'India', '56 Pine Ave, Abu Dhabi, UAE', 90000.00, 'Marketing Manager at ABC Inc.', 30000.00, 'classpath:bank_statements/bank_statement_john_doe.pdf',0);
