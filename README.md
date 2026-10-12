# Quiz Management System

A desktop quiz application built with **Java Swing**, **JDBC** and **MySQL**.
Administrators create quizzes and questions and manage users. Students register,
take quizzes and see their scores.

## Team

- Ayomide (write your teammates' names here)

## Features

- Login and student registration
- Role-based dashboard (ADMIN and STUDENT)
- Admin: manage quizzes, manage questions (with answer options), manage users
- Student: take a quiz one question at a time, go back and change answers,
  automatic scoring, view past results
- Admin can view every student's results
- Settings window to change your own name and password
- Clear message when the database is not running

## Requirements

- JDK 26 (the version set in `pom.xml`) or newer
- Apache NetBeans
- XAMPP (for MySQL and phpMyAdmin)
- Internet on the first run, so Maven can download the libraries

## Setup

1. **Start MySQL.** Open the XAMPP Control Panel and click **Start** next to MySQL.
2. **Create the database.** Open http://localhost/phpmyadmin (start the Apache module
   too if it does not load), click the **SQL** tab, paste the contents of
   `database.sql` and click **Go**. This creates `quiz_db` with all its tables.
3. **Open the project** in NetBeans: File > Open Project, then choose this folder.
4. **Check the connection settings** in `src/main/java/dao/DBConnection.java`.
   The defaults match a fresh XAMPP install:

   | Setting  | Value       |
   |----------|-------------|
   | Host     | `localhost` |
   | Port     | `3306`      |
   | Database | `quiz_db`   |
   | User     | `root`      |
   | Password | *(empty)*   |

5. **Run** the project (Shift+F6). The Login window should open.

## Default login

| Role  | Username | Password   |
|-------|----------|------------|
| Admin | `admin`  | `admin123` |

Students create their own accounts with the **Register** button on the login window.

## Project structure

```
src/main/java/
  com/ayomide/   QuizManagementSystem.java   (starts the app)
  model/         User, Quiz, Question, Option, Attempt
  dao/           DBConnection, UserDAO, QuizDAO, QuestionDAO, AttemptDAO
  ui/            LoginFrame, RegisterFrame, DashboardFrame,
                 ManageQuizzesFrame, ManageQuestionsFrame, ManageUsersFrame,
                 QuizTakeFrame, ResultsFrame, SettingsFrame
database.sql     (creates the database)
```

The windows never write SQL themselves. They call a DAO class, and the DAO talks to
the database.

## Troubleshooting

- **"Database not available" message:** MySQL is not running. Start it in XAMPP,
  check that `quiz_db` exists, and run the app again.
- **Red errors after a pull:** right-click the project and choose **Reload POM**.
- **A teammate's database looks different:** everyone must run `database.sql`
  on their own XAMPP.

## Notes

Passwords are stored as plain text to keep this school project simple. A real
application would store hashed passwords.
