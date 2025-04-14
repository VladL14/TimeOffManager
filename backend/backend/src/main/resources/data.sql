-- USERS
INSERT INTO users (name, email, role, is_active) VALUES ('Ana Popescu', 'ana@email.com', 'Admin', true);
INSERT INTO users (name, email, role, is_active) VALUES ('Ion Ionescu', 'ion@email.com', 'User', true);
INSERT INTO users (name, email, role, is_active) VALUES ('Maria Georgescu', 'maria@email.com', 'Manager', true);

-- PROJECTS
INSERT INTO projects (name, description, manager_id) VALUES ('Project Alpha', 'First major project', 3);
INSERT INTO projects (name, description, manager_id) VALUES ('Project Beta', 'Internal tool development', 3);

-- LEAVE TYPES
INSERT INTO leave_types (user_id, name, balance_days) VALUES (1, 'Vacation', 20);
INSERT INTO leave_types (user_id, name, balance_days) VALUES (2, 'Sick Leave', 10);

-- LEAVE REQUESTS
INSERT INTO leave_requests (user_id, leave_type_id, start_date, end_date, status, notes, approved_by) 
VALUES (1, 1, '2025-05-01', '2025-05-05', 'Approved', 'Family trip', 3);

INSERT INTO leave_requests (user_id, leave_type_id, start_date, end_date, status, notes, approved_by) 
VALUES (2, 2, '2025-04-20', '2025-04-22', 'Pending', 'Flu symptoms', NULL);

-- PROJECTS ASSIGNMENTS
INSERT INTO projects_assignments (user_id, project_id) VALUES (1, 1);
INSERT INTO projects_assignments (user_id, project_id) VALUES (2, 2);
INSERT INTO projects_assignments (user_id, project_id) VALUES (3, 1);
