CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100),
    role VARCHAR(50),
    is_active BOOLEAN
);

CREATE TABLE projects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    description VARCHAR(255),
    manager_id INT,
    FOREIGN KEY (manager_id) REFERENCES users(id)
);

CREATE TABLE leave_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    name VARCHAR(100),
    balance_days INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE leave_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    leave_type_id INT,
    start_date DATE,
    end_date DATE,
    status VARCHAR(50),
    notes VARCHAR(255),
    approved_by INT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (leave_type_id) REFERENCES leave_types(id),
    FOREIGN KEY (approved_by) REFERENCES users(id)
);

CREATE TABLE projects_assignments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    project_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (project_id) REFERENCES projects(id)
);
