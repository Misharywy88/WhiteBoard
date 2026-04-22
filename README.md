# WhiteBoard — Learning Management System

A desktop LMS built with Java Swing that supports three user roles: Student, Instructor, and Admin. Built as a university team project.

---

## What it does

WhiteBoard is a desktop application that handles the core workflows of a learning management system. Users log in with their role and get routed to their respective dashboard.

**Student:**
- View enrolled courses and announcements
- Download assignments
- Submit assignments
- Access student support

**Instructor:**
- Manage course content
- Upload and manage assignments
- View student submissions

**Admin:**
- Manage users (students and instructors)
- Approve or reject registration requests
- Manage course registrations
- Oversee the full system

---

## Tech stack

- **Java** — core language
- **Java Swing** — desktop UI
- **MySQL** — database for users, courses, and assignments
- **JDBC (MySQL Connector/J)** — database connectivity

---

## Project structure

```
WhiteBoard/
├── src/
│   ├── StudentPortalGUI.java      # App entry point
│   ├── LoginPanel.java            # Login with role-based routing
│   ├── RegisterPanel.java         # Registration with input validation
│   ├── WelcomePagePanel.java      # Welcome screen
│   ├── ForgotPasswordPanel.java   # Password recovery
│   ├── StudentMenu.java           # Student dashboard
│   ├── InstructorMenu.java        # Instructor dashboard
│   ├── AdminMenu.java             # Admin dashboard
│   ├── MySQLConnection.java       # Database connection handler
│   ├── AssignmentUploader.java    # Assignment upload logic
│   ├── AssignmentDownloader.java  # Assignment download logic
│   ├── ButtonEditor.java          # Custom table button editor
│   └── ButtonRenderer.java        # Custom table button renderer
└── lib/
    └── mysql-connector-j-9.7.0.jar  # MySQL JDBC driver (add manually)
```

---

## Getting started

### Prerequisites
- Java 17+
- MySQL Server
- MySQL Connector/J JAR (download from https://dev.mysql.com/downloads/connector/j/)

### Setup

**1. Clone the repository:**
```bash
git clone https://github.com/Misharywy88/WhiteBoard.git
cd WhiteBoard
```

**2. Add the MySQL Connector JAR:**
- Create a `lib/` folder in the project root
- Download `mysql-connector-j-9.7.0.jar` and place it inside `lib/`

**3. Set up MySQL database:**

Start MySQL and run the following SQL to create the database and all tables:

```sql
CREATE DATABASE elms;
USE elms;

CREATE TABLE Users (
    User_ID INT AUTO_INCREMENT PRIMARY KEY,
    Full_Name VARCHAR(100),
    Email VARCHAR(100) UNIQUE,
    Password VARCHAR(100),
    PhoneNumber VARCHAR(20),
    City VARCHAR(50),
    ZipCode VARCHAR(10),
    DateOfBirth DATE,
    Gender VARCHAR(10),
    GPA DOUBLE,
    Role VARCHAR(20),
    Username VARCHAR(100) UNIQUE,
    Street VARCHAR(100)
);

CREATE TABLE Student (
    Student_ID INT AUTO_INCREMENT PRIMARY KEY,
    User_ID INT,
    Student_Number VARCHAR(20),
    College_Name VARCHAR(100),
    Date_Of_Birth DATE,
    FOREIGN KEY (User_ID) REFERENCES Users(User_ID)
);

CREATE TABLE Instructor (
    Instructor_ID INT AUTO_INCREMENT PRIMARY KEY,
    User_ID INT,
    College_Name VARCHAR(100),
    FOREIGN KEY (User_ID) REFERENCES Users(User_ID)
);

CREATE TABLE Admin (
    Admin_ID INT AUTO_INCREMENT PRIMARY KEY,
    User_ID INT,
    College_Name VARCHAR(100),
    FOREIGN KEY (User_ID) REFERENCES Users(User_ID)
);

CREATE TABLE Course (
    Course_ID INT AUTO_INCREMENT PRIMARY KEY,
    CourseName VARCHAR(100),
    Description VARCHAR(255),
    Instructor_ID INT,
    Duration VARCHAR(50),
    FOREIGN KEY (Instructor_ID) REFERENCES Instructor(Instructor_ID)
);

CREATE TABLE Schedule (
    Schedule_ID INT AUTO_INCREMENT PRIMARY KEY,
    Course_ID INT,
    CourseName VARCHAR(100),
    Instructor_ID INT,
    Duration VARCHAR(50),
    Start_Date DATE,
    End_Date DATE,
    Schedule_Date DATE,
    Time VARCHAR(20),
    FOREIGN KEY (Course_ID) REFERENCES Course(Course_ID)
);

CREATE TABLE Enrollment (
    Enrollment_ID INT AUTO_INCREMENT PRIMARY KEY,
    Student_ID INT,
    Course_ID INT,
    Enrollment_Date DATE,
    Grade VARCHAR(10),
    FOREIGN KEY (Student_ID) REFERENCES Student(Student_ID),
    FOREIGN KEY (Course_ID) REFERENCES Course(Course_ID)
);

CREATE TABLE Assignment (
    Assignment_ID INT AUTO_INCREMENT PRIMARY KEY,
    Assignment_Name VARCHAR(100),
    Description VARCHAR(255),
    Course_ID INT,
    Due_Date DATE,
    FilePath VARCHAR(255),
    Assignment_File VARCHAR(255),
    FOREIGN KEY (Course_ID) REFERENCES Course(Course_ID)
);

CREATE TABLE Submission (
    Submission_ID INT AUTO_INCREMENT PRIMARY KEY,
    Assignment_ID INT,
    Student_ID INT,
    Submission_Date DATE,
    Submission_Status VARCHAR(20),
    FilePath VARCHAR(255),
    Grade VARCHAR(10),
    FOREIGN KEY (Assignment_ID) REFERENCES Assignment(Assignment_ID),
    FOREIGN KEY (Student_ID) REFERENCES Student(Student_ID)
);

CREATE TABLE StudentGrades (
    Grade_ID INT AUTO_INCREMENT PRIMARY KEY,
    Student_ID INT,
    Course_ID INT,
    Grade VARCHAR(10),
    User_ID INT,
    CourseName VARCHAR(100),
    FOREIGN KEY (Student_ID) REFERENCES Student(Student_ID),
    FOREIGN KEY (Course_ID) REFERENCES Course(Course_ID)
);

CREATE TABLE PendingRegistrations (
    Request_ID INT AUTO_INCREMENT PRIMARY KEY,
    User_ID INT,
    Course_ID INT,
    Status VARCHAR(20),
    Full_Name VARCHAR(100),
    Email VARCHAR(100),
    Role VARCHAR(20),
    RequestDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    DateOfBirth DATE,
    PhoneNumber VARCHAR(20),
    Username VARCHAR(100),
    Password VARCHAR(100),
    Gender VARCHAR(10),
    FOREIGN KEY (User_ID) REFERENCES Users(User_ID),
    FOREIGN KEY (Course_ID) REFERENCES Course(Course_ID)
);
```

**4. Create an Admin user:**
```sql
INSERT INTO Users (Full_Name, Email, Password, PhoneNumber, City, Role, Username)
VALUES ('Admin User', 'admin@test.com', 'admin123', '0501234567', 'Dammam', 'Admin', 'admin');

INSERT INTO Admin (User_ID) VALUES (1);
```

**5. Update database credentials:**

Open `src/MySQLConnection.java` and update the password to match your MySQL setup:
```java
String password = "1234"; // Change this to your MySQL root password
```

**6. Configure classpath in VS Code:**
- Open the project in VS Code
- Press Ctrl+Shift+P -> "Java: Configure Classpath"
- Under Libraries, add `lib/mysql-connector-j-9.7.0.jar`

**7. Run the app:**
- Open `src/StudentPortalGUI.java`
- Press Ctrl+F5 to run

---

## Registration flow

1. New users register through the app (email must end with @iau.edu.sa)
2. Registration requests appear in the Admin's Pending Registrations panel
3. Admin approves or rejects — approved users can then log in

## Notes

- Passwords must be 8+ characters with uppercase, lowercase, and a digit
- Phone numbers must start with 05 and be exactly 10 digits

---

Built as part of an Object-Oriented Programming course at Imam Abdulrahman Bin Faisal University. Team of 5 developers contributing to a 7,000+ line codebase.
