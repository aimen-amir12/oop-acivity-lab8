
package university.database;

import java.sql.*;

public class TableInitializer {

    public static void initializeDatabase() {
        System.out.println(" Initializing University Database...");

        try {
            createDatabase();

            // Create tables in correct order to handle foreign keys
            createPersonsTable();
            createStudentsTable();
            createFacultyTable();
            createStaffTable();
            createCoursesTable();
            createDepartmentsTable();
            createEnrollmentsTable();
            createDepartmentFacultyTable();
            createDepartmentCoursesTable();
            createLibraryBooksTable();
            createLibraryTransactionsTable();
            createPaymentsTable();
            createLoginCredentialsTable();

            System.out.println(" Database initialization complete!");

            // Insert sample data for testing
            insertSampleData();

        } catch (SQLException e) {
            System.out.println(" Database initialization failed: " + e.getMessage());
        }
    }

    private static void createDatabase() throws SQLException {
        String sql = "CREATE DATABASE IF NOT EXISTS university_db";
        try (Connection conn = getRootConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            stmt.executeUpdate("USE university_db");
        }
    }

    private static Connection getRootConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/", "root", "Java123.");
    }

    private static void createPersonsTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS persons (
                person_id VARCHAR(20) PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                email VARCHAR(100) UNIQUE,
                phone VARCHAR(20),
                person_type ENUM('Student', 'Faculty', 'Staff') NOT NULL,
                person_subtype VARCHAR(50),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                INDEX idx_person_type (person_type),
                INDEX idx_person_subtype (person_subtype)
            )
            """;
        executeUpdate(sql, "persons table");
    }

    private static void createStudentsTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS students (
                student_id VARCHAR(20) PRIMARY KEY,
                person_id VARCHAR(20) NOT NULL,
                advisor VARCHAR(100),
                gpa DECIMAL(3,2) DEFAULT 0.00,
                total_balance DECIMAL(10,2) DEFAULT 0.00,
                credits_earned INT DEFAULT 0,
                status ENUM('ACTIVE', 'INACTIVE', 'GRADUATED') DEFAULT 'ACTIVE',
                FOREIGN KEY (person_id) REFERENCES persons(person_id) ON DELETE CASCADE,
                FOREIGN KEY (student_id) REFERENCES persons(person_id) ON DELETE CASCADE
            )
            """;
        executeUpdate(sql, "students table");
    }

    private static void createFacultyTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS faculty (
                faculty_id VARCHAR(20) PRIMARY KEY,
                person_id VARCHAR(20) NOT NULL,
                salary DECIMAL(10,2) DEFAULT 0.00,
                total_balance DECIMAL(10,2) DEFAULT 0.00,
                department VARCHAR(100),
                status ENUM('ACTIVE', 'INACTIVE', 'ON_LEAVE') DEFAULT 'ACTIVE',
                FOREIGN KEY (person_id) REFERENCES persons(person_id) ON DELETE CASCADE,
                FOREIGN KEY (faculty_id) REFERENCES persons(person_id) ON DELETE CASCADE
            )
            """;
        executeUpdate(sql, "faculty table");
    }

    private static void createStaffTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS staff (
                staff_id VARCHAR(20) PRIMARY KEY,
                person_id VARCHAR(20) NOT NULL,
                salary DECIMAL(10,2) DEFAULT 0.00,
                total_balance DECIMAL(10,2) DEFAULT 0.00,
                position VARCHAR(100),
                FOREIGN KEY (person_id) REFERENCES persons(person_id) ON DELETE CASCADE,
                FOREIGN KEY (staff_id) REFERENCES persons(person_id) ON DELETE CASCADE
            )
            """;
        executeUpdate(sql, "staff table");
    }

    private static void createCoursesTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS courses (
                course_code VARCHAR(20) PRIMARY KEY,
                title VARCHAR(100) NOT NULL,
                credits INT DEFAULT 3,
                capacity INT DEFAULT 50,
                current_enrollment INT DEFAULT 0,
                department VARCHAR(100),
                semester VARCHAR(20),
                faculty_id VARCHAR(20),
                status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
                FOREIGN KEY (faculty_id) REFERENCES faculty(faculty_id) ON DELETE SET NULL
            )
            """;
        executeUpdate(sql, "courses table");
    }

    private static void createEnrollmentsTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS enrollments (
                enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
                student_id VARCHAR(20) NOT NULL,
                course_code VARCHAR(20) NOT NULL,
                enrollment_date DATE DEFAULT (CURDATE()),
                grade DECIMAL(4,2),
                status ENUM('ENROLLED', 'COMPLETED', 'DROPPED') DEFAULT 'ENROLLED',
                FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
                FOREIGN KEY (course_code) REFERENCES courses(course_code) ON DELETE CASCADE,
                UNIQUE KEY unique_enrollment (student_id, course_code),
                INDEX idx_student_id (student_id),
                INDEX idx_course_code (course_code),
                INDEX idx_status (status)
            )
            """;
        executeUpdate(sql, "enrollments table");
    }

    private static void createDepartmentsTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS departments (
                dept_id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL UNIQUE,
                budget DECIMAL(12,2) DEFAULT 0.00,
                head_faculty_id VARCHAR(20),
                established_year INT,
                FOREIGN KEY (head_faculty_id) REFERENCES faculty(faculty_id) ON DELETE SET NULL
            )
            """;
        executeUpdate(sql, "departments table");
    }

    private static void createDepartmentFacultyTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS department_faculty (
                dept_id INT NOT NULL,
                faculty_id VARCHAR(20) NOT NULL,
                PRIMARY KEY (dept_id, faculty_id),
                FOREIGN KEY (dept_id) REFERENCES departments(dept_id) ON DELETE CASCADE,
                FOREIGN KEY (faculty_id) REFERENCES faculty(faculty_id) ON DELETE CASCADE
            )
            """;
        executeUpdate(sql, "department_faculty table");
    }

    private static void createDepartmentCoursesTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS department_courses (
                dept_id INT NOT NULL,
                course_code VARCHAR(20) NOT NULL,
                PRIMARY KEY (dept_id, course_code),
                FOREIGN KEY (dept_id) REFERENCES departments(dept_id) ON DELETE CASCADE,
                FOREIGN KEY (course_code) REFERENCES courses(course_code) ON DELETE CASCADE
            )
            """;
        executeUpdate(sql, "department_courses table");
    }

    private static void createLibraryBooksTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS library_books (
                book_id INT AUTO_INCREMENT PRIMARY KEY,
                title VARCHAR(200) NOT NULL,
                author VARCHAR(100),
                isbn VARCHAR(20) UNIQUE,
                publication_year INT,
                is_borrowed BOOLEAN DEFAULT FALSE,
                added_date DATE DEFAULT (CURDATE()),
                INDEX idx_title (title),
                INDEX idx_author (author),
                INDEX idx_is_borrowed (is_borrowed)
            )
            """;
        executeUpdate(sql, "library_books table");
    }

    private static void createLibraryTransactionsTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS library_transactions (
                transaction_id INT AUTO_INCREMENT PRIMARY KEY,
                book_id INT NOT NULL,
                borrower_id VARCHAR(20) NOT NULL,
                borrow_date DATE NOT NULL,
                due_date DATE NOT NULL,
                return_date DATE,
                fine DECIMAL(8,2) DEFAULT 0.00,
                status ENUM('BORROWED', 'RETURNED', 'OVERDUE') DEFAULT 'BORROWED',
                FOREIGN KEY (book_id) REFERENCES library_books(book_id) ON DELETE CASCADE,
                FOREIGN KEY (borrower_id) REFERENCES persons(person_id) ON DELETE CASCADE,
                INDEX idx_borrower_id (borrower_id),
                INDEX idx_status (status),
                INDEX idx_due_date (due_date)
            )
            """;
        executeUpdate(sql, "library_transactions table");
    }

    private static void createPaymentsTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS payments (
                payment_id INT AUTO_INCREMENT PRIMARY KEY,
                payer_id VARCHAR(20),
                payee_id VARCHAR(20),
                amount DECIMAL(10,2) NOT NULL,
                payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                payment_type ENUM('TUITION_PAYMENT', 'SALARY_PAYMENT', 'FINE_PAYMENT', 'OTHER') NOT NULL,
                description VARCHAR(200),
                transaction_id VARCHAR(50),
                FOREIGN KEY (payer_id) REFERENCES persons(person_id) ON DELETE SET NULL,
                FOREIGN KEY (payee_id) REFERENCES persons(person_id) ON DELETE SET NULL,
                INDEX idx_payer_id (payer_id),
                INDEX idx_payee_id (payee_id),
                INDEX idx_payment_date (payment_date),
                INDEX idx_payment_type (payment_type)
            )
            """;
        executeUpdate(sql, "payments table");
    }

    private static void createLoginCredentialsTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS login_credentials (
                user_id VARCHAR(20) PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                password_hash VARCHAR(255) NOT NULL,
                role ENUM('STUDENT', 'FACULTY', 'STAFF', 'ADMIN') NOT NULL,
                is_active BOOLEAN DEFAULT TRUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                last_login TIMESTAMP NULL,
                FOREIGN KEY (user_id) REFERENCES persons(person_id) ON DELETE CASCADE
            )
            """;
        executeUpdate(sql, "login_credentials table");
    }

    private static void executeUpdate(String sql, String tableName) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println(" Created " + tableName);
        } catch (SQLException e) {
            if (e.getMessage().contains("already exists")) {
                System.out.println( tableName + " already exists");
            } else {
                throw e;
            }
        }
    }

    private static void insertSampleData() throws SQLException {
        System.out.println("\n Inserting sample data...");

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Check if data already exists
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM persons");
            rs.next();
            if (rs.getInt(1) > 0) {
                System.out.println("  Sample data already exists");
                return;
            }

            // Insert sample persons
            String[] personInserts = {
                    "INSERT INTO persons (person_id, name, email, phone, person_type, person_subtype) VALUES " +
                            "('S1001', 'Ali Ahmed', 'ali.ahmed@uni.edu', '0300-1234567', 'Student', 'Undergraduate')," +
                            "('S1002', 'Sara Khan', 'sara.khan@uni.edu', '0300-2345678', 'Student', 'Graduate')," +
                            "('S1003', 'Omar Farooq', 'omar.farooq@uni.edu', '0300-3456789', 'Student', 'PhD Student')," +
                            "('F2001', 'Dr. Ahmed Raza', 'ahmed.raza@uni.edu', '0300-4567890', 'Faculty', 'Professor')," +
                            "('F2002', 'Dr. Fatima Shah', 'fatima.shah@uni.edu', '0300-5678901', 'Faculty', 'Assistant Professor')," +
                            "('F2003', 'Ms. Ayesha Malik', 'ayesha.malik@uni.edu', '0300-6789012', 'Faculty', 'Lecturer')," +
                            "('ST001', 'Admin User', 'admin@uni.edu', '0300-7890123', 'Staff', 'Administrator')"
            };

            for (String insert : personInserts) {
                stmt.executeUpdate(insert);
            }

            // Insert sample students
            String[] studentInserts = {
                    "INSERT INTO students (student_id, person_id, advisor, gpa, total_balance) VALUES " +
                            "('S1001', 'S1001', NULL, 3.2, 50000.00)," +
                            "('S1002', 'S1002', 'Dr. Ahmed Raza', 3.8, 75000.00)," +
                            "('S1003', 'S1003', 'Dr. Fatima Shah', 3.9, 100000.00)"
            };

            for (String insert : studentInserts) {
                stmt.executeUpdate(insert);
            }

            // Insert sample faculty
            String[] facultyInserts = {
                    "INSERT INTO faculty (faculty_id, person_id, salary, department) VALUES " +
                            "('F2001', 'F2001', 150000.00, 'Computer Science')," +
                            "('F2002', 'F2002', 120000.00, 'Computer Science')," +
                            "('F2003', 'F2003', 80000.00, 'Mathematics')"
            };

            for (String insert : facultyInserts) {
                stmt.executeUpdate(insert);
            }

            // Insert sample courses
            String[] courseInserts = {
                    "INSERT INTO courses (course_code, title, credits, capacity, department) VALUES " +
                            "('CS101', 'Introduction to Programming', 3, 50, 'Computer Science')," +
                            "('CS201', 'Data Structures', 3, 40, 'Computer Science')," +
                            "('MATH101', 'Calculus I', 3, 60, 'Mathematics')," +
                            "('CS301', 'Database Systems', 3, 35, 'Computer Science')"
            };

            for (String insert : courseInserts) {
                stmt.executeUpdate(insert);
            }

            // Insert sample enrollments
            String[] enrollmentInserts = {
                    "INSERT INTO enrollments (student_id, course_code, grade) VALUES " +
                            "('S1001', 'CS101', 85.5)," +
                            "('S1001', 'MATH101', 78.0)," +
                            "('S1002', 'CS201', 92.0)," +
                            "('S1002', 'CS301', 88.5)"
            };

            for (String insert : enrollmentInserts) {
                stmt.executeUpdate(insert);
            }

            // Insert sample library books
            String[] bookInserts = {
                    "INSERT INTO library_books (title, author, isbn, publication_year) VALUES " +
                            "('Introduction to Java Programming', 'Daniel Liang', '9780134670942', 2017)," +
                            "('Database System Concepts', 'Abraham Silberschatz', '9780078022159', 2010)," +
                            "('Clean Code', 'Robert C. Martin', '9780132350884', 2008)"
            };

            for (String insert : bookInserts) {
                stmt.executeUpdate(insert);
            }

            // Insert sample departments
            String[] deptInserts = {
                    "INSERT INTO departments (name, budget, established_year) VALUES " +
                            "('Computer Science', 5000000.00, 2000)," +
                            "('Mathematics', 3000000.00, 2000)," +
                            "('Physics', 2500000.00, 2005)"
            };

            for (String insert : deptInserts) {
                stmt.executeUpdate(insert);
            }

            System.out.println(" Sample data inserted successfully!");

        } catch (SQLException e) {
            System.out.println("️Error inserting sample data: " + e.getMessage());
        }
    }
}