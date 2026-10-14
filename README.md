# Quiz Management System (QMS)

A professional desktop quiz management and assessment application built with **Java Swing (FlatLaf Modern Design System)**, **JDBC**, and **MySQL**.

Supports role-based administration (Admin & Student), interactive timed assessments with live progress tracking, question authoring with 4-choice validation, comprehensive score tracking, and profile management.

---

## ✨ Features

- **Modern & Clean UI**: Custom design system built on **FlatLaf** featuring card layouts, smooth focus states, cohesive color palettes, responsive typography, and generous table row heights.
- **Role-Based Access**:
  - **Administrator**:
    - **Live Dashboard**: High-level metrics (Active Quizzes, Total Questions, Registered Students, Total Attempts).
    - **Quiz Management**: Create, edit, search, and delete quizzes with live keyword filtering.
    - **Question Management**: Full multiple-choice question authoring with automatic answer option validation.
    - **User Administration**: View all registered students, assign roles, and reset passwords.
    - **Gradebook & Reports**: Search and view test history and performance across all students.
  - **Student**:
    - **Interactive Assessments**: Clean card-based question cards with previous/next navigation, progress bar, answered counters, and instant scoring.
    - **Personal Performance**: View past scores, percentages, and completed dates.
    - **Account Settings**: Update display name and change password securely.
- **Robust Database Layer**: Clean DAO separation with foreign key cascading and transaction safety.
- **Convenient Windows Launcher**: One-click `run.bat` launcher that checks MySQL status and runs the application.

---

## 🚀 Quick Start

### 1. Requirements
- **Java SE Development Kit (JDK 17 LTS)** or newer
- **MySQL Server** (e.g. via XAMPP on port `3306`)

### 2. Database Setup
1. Ensure MySQL is running on `localhost:3306`.
2. Run or import `database.sql` into MySQL. This creates `quiz_db` with all necessary tables, cascading foreign keys, and seed sample quizzes.
   ```powershell
   # If using MySQL command line:
   mysql -u root -e "SOURCE database.sql;"
   ```

### 3. Run the Application
You can run the application in any of the following ways:

#### Option A: One-Click Script (Recommended)
Double-click `run.bat` in the project root or run in PowerShell:
```powershell
.\run.bat
```

#### Option B: Standalone JAR
```powershell
java -jar target/QuizManagementSystem-1.0-SNAPSHOT.jar
```

#### Option C: Maven Command
```powershell
mvn clean compile exec:java
```

---

## 🔑 Default Accounts

| Role | Username | Password | Notes |
|---|---|---|---|
| **Admin** | `admin` | `admin123` | Full administrative control |
| **Student** | `sarah` | `student123` | Sample student with test records |
| **Student** | `alex` | `student123` | Sample student |

> New students can also click **"Create New Account"** on the login screen to register instantly.

---

## 📁 Project Architecture

```
QuizManagementSystem/
├── run.bat                          # One-click Windows application launcher
├── pom.xml                          # Maven build definition (Java 17, FlatLaf, MySQL Connector)
├── database.sql                     # Database schema, foreign keys, and sample seed data
└── src/main/java/
    ├── com/quiz/
    │   └── QuizManagementSystem.java  # Main application launcher & bootstrap
    ├── model/
    │   ├── User.java                # User model (Admin / Student)
    │   ├── Quiz.java                # Quiz entity
    │   ├── Question.java            # Question entity with options list
    │   ├── Option.java              # Answer choice entity
    │   └── Attempt.java             # Submission entity & scoring
    ├── dao/
    │   ├── DBConnection.java        # JDBC connection pooling & availability probe
    │   ├── UserDAO.java             # User login, registration, and management
    │   ├── QuizDAO.java             # Quiz CRUD & counting
    │   ├── QuestionDAO.java         # Question CRUD, batch options & counts
    │   └── AttemptDAO.java          # Quiz scoring, submissions, and metrics
    └── ui/
        ├── UITheme.java             # Design system tokens, styles, cards, and tables
        ├── LoginFrame.java          # Modern auth card with Enter-key submit
        ├── RegisterFrame.java       # Student account creation
        ├── DashboardFrame.java      # Metric cards & role-based quick action cards
        ├── ManageQuizzesFrame.java  # Quiz list with real-time filter & form
        ├── ManageQuestionsFrame.java# Question & 4-option authoring editor
        ├── ManageUsersFrame.java    # Account management table & editor
        ├── QuizTakeFrame.java       # Interactive quiz taking & scoring dialog
        ├── ResultsFrame.java        # Gradebook with search and score stats
        └── SettingsFrame.java       # Profile update and password change
```
