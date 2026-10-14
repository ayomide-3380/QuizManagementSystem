-- Quiz Management System Database Schema & Seed Data
-- Import this file in phpMyAdmin or run it in MySQL.

CREATE DATABASE IF NOT EXISTS quiz_db;
USE quiz_db;

-- 1. Roles table
CREATE TABLE IF NOT EXISTS roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

-- 2. Users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role_id INT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT
);

-- 3. Quizzes table
CREATE TABLE IF NOT EXISTS quizzes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description VARCHAR(255),
    created_by INT,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 4. Questions table
CREATE TABLE IF NOT EXISTS questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    quiz_id INT NOT NULL,
    question_text VARCHAR(500) NOT NULL,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
);

-- 5. Options table
CREATE TABLE IF NOT EXISTS options (
    id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT NOT NULL,
    option_text VARCHAR(255) NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

-- 6. Quiz Attempts table
CREATE TABLE IF NOT EXISTS quiz_attempts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    quiz_id INT NOT NULL,
    score INT NOT NULL,
    total INT NOT NULL,
    attempted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
);

-- 7. Answers table
CREATE TABLE IF NOT EXISTS answers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    attempt_id INT NOT NULL,
    question_id INT NOT NULL,
    option_id INT,
    FOREIGN KEY (attempt_id) REFERENCES quiz_attempts(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

-- Seed Roles
INSERT IGNORE INTO roles (id, name) VALUES (1, 'ADMIN'), (2, 'STUDENT');

-- Seed Default Accounts
INSERT IGNORE INTO users (id, full_name, username, password, role_id)
VALUES 
    (1, 'System Administrator', 'admin', 'admin123', 1),
    (2, 'Sarah Jenkins', 'sarah', 'student123', 2),
    (3, 'Alex Rivera', 'alex', 'student123', 2);

-- Seed Quizzes
INSERT IGNORE INTO quizzes (id, title, description, created_by) VALUES 
    (1, 'Java Programming Fundamentals', 'Test your core Java knowledge including OOP, types, memory, and syntax.', 1),
    (2, 'Database & SQL Essentials', 'Evaluate your understanding of relational databases, queries, and constraints.', 1),
    (3, 'General Computing & Web Basics', 'Covers fundamental concepts of networking, HTTP, algorithms, and computers.', 1),
    (4, 'OOP & Design Patterns', 'Master Object-Oriented principles, SOLID design, and reusable software patterns.', 1),
    (5, 'Data Structures & Algorithms', 'Assess key knowledge in stacks, queues, hash maps, trees, and algorithmic complexity.', 1),
    (6, 'Git & Version Control Mastery', 'Test command line Git proficiency, branching strategies, and collaboration workflows.', 1),
    (7, 'Web Security & Networking', 'Test concepts in TLS/HTTPS, cyber vulnerabilities, DNS, and HTTP headers.', 1),
    (8, 'Python Programming Essentials', 'Explore Python data structures, syntax, generators, and idiomatic practices.', 1);

-- ==================== QUESTIONS & OPTIONS ====================

-- Quiz 1: Java
INSERT IGNORE INTO questions (id, quiz_id, question_text) VALUES
    (1, 1, 'Which keyword in Java is used to inherit a class?'),
    (2, 1, 'Which data type is used to create a variable that should store text?'),
    (3, 1, 'What is the default value of a boolean variable in Java?'),
    (4, 1, 'Which Java package is imported by default in all Java programs?'),
    (5, 1, 'Which method is the starting entry point for any Java standalone application?');

INSERT IGNORE INTO options (id, question_id, option_text, is_correct) VALUES
    (1, 1, 'implements', FALSE), (2, 1, 'extends', TRUE), (3, 1, 'inherits', FALSE), (4, 1, 'instanceof', FALSE),
    (5, 2, 'String', TRUE), (6, 2, 'char', FALSE), (7, 2, 'Txt', FALSE), (8, 2, 'Text', FALSE),
    (9, 3, 'true', FALSE), (10, 3, 'false', TRUE), (11, 3, 'null', FALSE), (12, 3, '0', FALSE),
    (13, 4, 'java.util', FALSE), (14, 4, 'java.lang', TRUE), (15, 4, 'java.io', FALSE), (16, 4, 'java.net', FALSE),
    (17, 5, 'start()', FALSE), (18, 5, 'main(String[] args)', TRUE), (19, 5, 'run()', FALSE), (20, 5, 'init()', FALSE);

-- Quiz 2: SQL
INSERT IGNORE INTO questions (id, quiz_id, question_text) VALUES
    (6, 2, 'Which SQL statement is used to extract data from a database?'),
    (7, 2, 'Which SQL clause is used to filter records based on a specified condition?'),
    (8, 2, 'What does the primary key constraint uniquely identify in a table?'),
    (9, 2, 'Which statement is used to remove all records from a table quickly without logging individual row deletions?');

INSERT IGNORE INTO options (id, question_id, option_text, is_correct) VALUES
    (21, 6, 'EXTRACT', FALSE), (22, 6, 'SELECT', TRUE), (23, 6, 'GET', FALSE), (24, 6, 'PULL', FALSE),
    (25, 7, 'WHERE', TRUE), (26, 7, 'ORDER BY', FALSE), (27, 7, 'GROUP BY', FALSE), (28, 7, 'HAVING', FALSE),
    (29, 8, 'A row (record)', TRUE), (30, 8, 'A column (attribute)', FALSE), (31, 8, 'An index name', FALSE), (32, 8, 'A foreign database', FALSE),
    (33, 9, 'DROP TABLE', FALSE), (34, 9, 'TRUNCATE TABLE', TRUE), (35, 9, 'REMOVE ALL', FALSE), (36, 9, 'DELETE INSTANT', FALSE);

-- Quiz 3: Computing & Web
INSERT IGNORE INTO questions (id, quiz_id, question_text) VALUES
    (10, 3, 'What does HTTP stand for?'),
    (11, 3, 'Which status code indicates a successful HTTP request?'),
    (12, 3, 'What is the time complexity of binary search on a sorted array of size n?');

INSERT IGNORE INTO options (id, question_id, option_text, is_correct) VALUES
    (37, 10, 'HyperText Transfer Protocol', TRUE), (38, 10, 'HyperText Transmission Process', FALSE), (39, 10, 'High Transfer Technology Protocol', FALSE), (40, 10, 'Home Tool Transfer Protocol', FALSE),
    (41, 11, '404', FALSE), (42, 11, '500', FALSE), (43, 11, '200', TRUE), (44, 11, '301', FALSE),
    (45, 12, 'O(n)', FALSE), (46, 12, 'O(log n)', TRUE), (47, 12, 'O(1)', FALSE), (48, 12, 'O(n^2)', FALSE);

-- Quiz 4: OOP & Design Patterns
INSERT IGNORE INTO questions (id, quiz_id, question_text) VALUES
    (13, 4, 'Which OOP principle focuses on bundling data and methods while restricting direct access to internal state?'),
    (14, 4, 'What occurs when a subclass provides a specific implementation of a method declared in its superclass?'),
    (15, 4, 'Which design pattern guarantees that a class has only one instance and provides a global access point?'),
    (16, 4, 'What does the Open/Closed Principle (in SOLID) state?'),
    (17, 4, 'Which design pattern defines a family of algorithms, encapsulates each one, and makes them interchangeable?');

INSERT IGNORE INTO options (id, question_id, option_text, is_correct) VALUES
    (49, 13, 'Inheritance', FALSE), (50, 13, 'Encapsulation', TRUE), (51, 13, 'Polymorphism', FALSE), (52, 13, 'Composition', FALSE),
    (53, 14, 'Method Overloading', FALSE), (54, 14, 'Method Overriding', TRUE), (55, 14, 'Method Shadowing', FALSE), (56, 14, 'Data Hiding', FALSE),
    (57, 15, 'Factory Pattern', FALSE), (58, 15, 'Singleton Pattern', TRUE), (59, 15, 'Prototype Pattern', FALSE), (60, 15, 'Builder Pattern', FALSE),
    (61, 16, 'Classes should be open for modification and closed for extension', FALSE),
    (62, 16, 'Software entities should be open for extension, but closed for modification', TRUE),
    (63, 16, 'Methods should always be declared public or protected', FALSE),
    (64, 16, 'Objects should be closed to inheritance', FALSE),
    (65, 17, 'Strategy Pattern', TRUE), (66, 17, 'Observer Pattern', FALSE), (67, 17, 'Adapter Pattern', FALSE), (68, 17, 'Decorator Pattern', FALSE);

-- Quiz 5: Data Structures & Algorithms
INSERT IGNORE INTO questions (id, quiz_id, question_text) VALUES
    (18, 5, 'Which data structure operates according to the Last-In, First-Out (LIFO) principle?'),
    (19, 5, 'What is the average time complexity for lookup/search in a well-distributed Hash Table?'),
    (20, 5, 'Which sorting algorithm guarantees O(n log n) worst-case time complexity and is stable?'),
    (21, 5, 'Which data structure is typically utilized when executing a Breadth-First Search (BFS)?'),
    (22, 5, 'What is the search time complexity in a balanced Binary Search Tree (e.g. AVL or Red-Black Tree)?');

INSERT IGNORE INTO options (id, question_id, option_text, is_correct) VALUES
    (69, 18, 'Queue', FALSE), (70, 18, 'Stack', TRUE), (71, 18, 'Linked List', FALSE), (72, 18, 'Heap', FALSE),
    (73, 19, 'O(1)', TRUE), (74, 19, 'O(log n)', FALSE), (75, 19, 'O(n)', FALSE), (76, 19, 'O(n log n)', FALSE),
    (77, 20, 'Quick Sort', FALSE), (78, 20, 'Bubble Sort', FALSE), (79, 20, 'Merge Sort', TRUE), (80, 20, 'Selection Sort', FALSE),
    (81, 21, 'Stack', FALSE), (82, 21, 'Queue', TRUE), (83, 21, 'Priority Queue', FALSE), (84, 21, 'Hash Set', FALSE),
    (85, 22, 'O(1)', FALSE), (86, 22, 'O(log n)', TRUE), (87, 22, 'O(n)', FALSE), (88, 22, 'O(n^2)', FALSE);

-- Quiz 6: Git & Version Control
INSERT IGNORE INTO questions (id, quiz_id, question_text) VALUES
    (23, 6, 'Which command saves your staged snapshots to the local Git repository history?'),
    (24, 6, 'What is the core distinction between git merge and git rebase?'),
    (25, 6, 'Which command applies the changes introduced by a specific existing commit from another branch into your branch?'),
    (26, 6, 'What does HEAD represent in Git?'),
    (27, 6, 'Which command temporarily stores uncommitted changes without committing them, leaving a clean working directory?');

INSERT IGNORE INTO options (id, question_id, option_text, is_correct) VALUES
    (89, 23, 'git stage', FALSE), (90, 23, 'git commit', TRUE), (91, 23, 'git push', FALSE), (92, 23, 'git stash', FALSE),
    (93, 24, 'Rebase rewrites linear history, merge preserves history with a merge commit', TRUE),
    (94, 24, 'Merge deletes branches, rebase does not', FALSE),
    (95, 24, 'Rebase only works with remote repositories', FALSE),
    (96, 24, 'There is no functional difference', FALSE),
    (97, 25, 'git cherry-pick', TRUE), (98, 25, 'git pull --force', FALSE), (99, 25, 'git checkout -b', FALSE), (100, 25, 'git rebase --apply', FALSE),
    (101, 26, 'The primary remote repository URL', FALSE),
    (102, 26, 'A pointer to the current branch or commit currently checked out', TRUE),
    (103, 26, 'The root master branch', FALSE),
    (104, 26, 'The most recent tag', FALSE),
    (105, 27, 'git pause', FALSE), (106, 27, 'git stash', TRUE), (107, 27, 'git store', FALSE), (108, 27, 'git hold', FALSE);

-- Quiz 7: Web Security & Networking
INSERT IGNORE INTO questions (id, quiz_id, question_text) VALUES
    (28, 7, 'What cryptographic protocol powers encryption in HTTPS connections?'),
    (29, 7, 'Which vulnerability involves malicious execution of attacker-supplied database queries?'),
    (30, 7, 'What does the HTTP 403 Forbidden status code indicate?'),
    (31, 7, 'What is the primary function of DNS on computer networks?'),
    (32, 7, 'What browser mechanism restricts how a document or script loaded by one origin can interact with a resource from another?');

INSERT IGNORE INTO options (id, question_id, option_text, is_correct) VALUES
    (109, 28, 'TLS / SSL', TRUE), (110, 28, 'FTP', FALSE), (111, 28, 'SSH2', FALSE), (112, 28, 'MD5', FALSE),
    (113, 29, 'Cross-Site Scripting (XSS)', FALSE), (114, 29, 'SQL Injection (SQLi)', TRUE), (115, 29, 'Buffer Overflow', FALSE), (116, 29, 'DDoS', FALSE),
    (117, 30, 'The resource was permanently deleted', FALSE),
    (118, 30, 'The server understands the request but refuses to authorize it', TRUE),
    (119, 30, 'The server timed out', FALSE),
    (120, 30, 'Invalid server gateway', FALSE),
    (121, 31, 'Resolving human-friendly domain names to IP addresses', TRUE),
    (122, 31, 'Encrypting packet payloads', FALSE),
    (123, 31, 'Allocating MAC addresses to network switches', FALSE),
    (124, 31, 'Compressing HTTP bodies', FALSE),
    (125, 32, 'Same-Origin Policy (SOP)', TRUE), (126, 32, 'Content Delivery Protocol', FALSE), (127, 32, 'Firewall Rule 80', FALSE), (128, 32, 'DNS Securing System', FALSE);

-- Quiz 8: Python Programming
INSERT IGNORE INTO questions (id, quiz_id, question_text) VALUES
    (33, 8, 'Which of the following built-in Python data types is immutable?'),
    (34, 8, 'What is the syntax for a list comprehension that squares integers from 0 to 4?'),
    (35, 8, 'Which keyword turns a regular Python function into a generator?'),
    (36, 8, 'What is the role of __init__ in a Python class?'),
    (37, 8, 'How are code blocks and scopes defined in Python syntax?');

INSERT IGNORE INTO options (id, question_id, option_text, is_correct) VALUES
    (129, 33, 'list', FALSE), (130, 33, 'tuple', TRUE), (131, 33, 'dict', FALSE), (132, 33, 'set', FALSE),
    (133, 34, '[x**2 for x in range(5)]', TRUE), (134, 34, '{x^2 in range(5)}', FALSE), (135, 34, '[for x in range(5): x*x]', FALSE), (136, 34, 'map(x**2, range(5))[]', FALSE),
    (137, 35, 'return', FALSE), (138, 35, 'yield', TRUE), (139, 35, 'generate', FALSE), (140, 35, 'produce', FALSE),
    (141, 36, 'It is the destructor called during garbage collection', FALSE),
    (142, 36, 'It serves as the constructor to initialize newly created object attributes', TRUE),
    (143, 36, 'It compiles Python bytecode', FALSE),
    (144, 36, 'It defines class inheritance', FALSE),
    (145, 37, 'Curly braces { }', FALSE), (146, 37, 'Whitespace and Indentation', TRUE), (147, 37, 'Begin / End keywords', FALSE), (148, 37, 'Semicolons at line ends', FALSE);

-- Add some sample initial student attempts so results and stats look alive!
INSERT IGNORE INTO quiz_attempts (id, user_id, quiz_id, score, total, attempted_at) VALUES
    (1, 2, 1, 5, 5, NOW() - INTERVAL 2 DAY),
    (2, 2, 2, 4, 4, NOW() - INTERVAL 1 DAY),
    (3, 3, 1, 4, 5, NOW() - INTERVAL 3 HOUR),
    (4, 3, 3, 3, 3, NOW() - INTERVAL 1 HOUR);