CREATE DATABASE cms;

USE cms;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL,
    role ENUM('Tester', 'Junior Developer', 'Senior Developer', 'Manager') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE contacts (
    contact_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100) NOT NULL,
    nickname VARCHAR(100),
    phone_primary VARCHAR(20) NOT NULL,
    phone_secondary VARCHAR(20),
    email VARCHAR(150) NOT NULL,
    linkedin_url VARCHAR(255),
    birth_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

SELECT * FROM contacts;


INSERT INTO users (username, password_hash, name, surname, role) VALUES
('tt', SHA2('tt', 256), 'Test', 'Tester', 'Tester'),
('jd', SHA2('jd', 256), 'Junior', 'Dev', 'Junior Developer'),
('sd', SHA2('sd', 256), 'Senior', 'Dev', 'Senior Developer'),
('man', SHA2('man', 256), 'Manager', 'Boss', 'Manager');





