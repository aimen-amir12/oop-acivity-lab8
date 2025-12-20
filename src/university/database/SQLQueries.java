package university.database;

public class SQLQueries {
    // PERSON QUERIES
    public static final String INSERT_PERSON = """
        INSERT INTO persons (person_id, name, email, phone, person_type, person_subtype) 
        VALUES (?, ?, ?, ?, ?, ?)
        """;

    public static final String SELECT_PERSON_BY_ID = """
        SELECT * FROM persons WHERE person_id = ?
        """;

    public static final String UPDATE_PERSON = """
        UPDATE persons SET name = ?, email = ?, phone = ? WHERE person_id = ?
        """;

    public static final String DELETE_PERSON = """
        DELETE FROM persons WHERE person_id = ?
        """;

    public static final String SEARCH_PERSONS_BY_NAME = """
    SELECT * FROM persons 
    WHERE name LIKE ? 
    AND (person_type = ? OR ? IS NULL)
    ORDER BY name
    LIMIT ?
    """;


    public static final String SELECT_FACULTY_BY_DEPARTMENT = """
    SELECT f.faculty_id, p.name, p.email, f.salary, f.department
    FROM faculty f
    JOIN persons p ON f.person_id = p.person_id
    WHERE f.department LIKE ?
    ORDER BY p.name
    """;

    // STUDENT QUERIES
    public static final String INSERT_STUDENT = """
        INSERT INTO students (student_id, person_id, advisor, gpa, total_balance) 
        VALUES (?, ?, ?, ?, ?)
        """;

    public static final String SELECT_STUDENT_BY_ID = """
        SELECT s.*, p.* FROM students s 
        JOIN persons p ON s.person_id = p.person_id 
        WHERE s.student_id = ?
        """;

    public static final String UPDATE_STUDENT_GPA = """
        UPDATE students SET gpa = ?, total_balance = ? WHERE student_id = ?
        """;

    public static final String SELECT_ALL_STUDENTS = """
        SELECT s.*, p.* FROM students s 
        JOIN persons p ON s.person_id = p.person_id 
        ORDER BY s.student_id
        """;

    public static final String SELECT_STUDENTS_BY_COURSE = """
    SELECT s.student_id, p.name, p.email, e.grade, e.enrollment_date
    FROM enrollments e
    JOIN students s ON e.student_id = s.student_id
    JOIN persons p ON s.person_id = p.person_id
    WHERE e.course_code = ? AND e.status = 'ENROLLED'
    ORDER BY p.name
    """;


    // COURSE QUERIES
    public static final String INSERT_COURSE = """
        INSERT INTO courses (course_code, title, credits, capacity, department) 
        VALUES (?, ?, ?, ?, ?)
        """;

    public static final String SELECT_COURSE_BY_CODE = """
        SELECT * FROM courses WHERE course_code = ?
        """;

    public static final String UPDATE_COURSE_CAPACITY = """
        UPDATE courses SET capacity = ?, current_enrollment = ? WHERE course_code = ?
        """;

    public static final String SELECT_ALL_COURSES = """
        SELECT * FROM courses ORDER BY course_code
        """;

    // ENROLLMENT QUERIES
    public static final String INSERT_ENROLLMENT = """
        INSERT INTO enrollments (student_id, course_code, enrollment_date) 
        VALUES (?, ?, CURDATE())
        """;

    public static final String SELECT_ENROLLMENTS_BY_STUDENT = """
        SELECT e.*, c.title FROM enrollments e 
        JOIN courses c ON e.course_code = c.course_code 
        WHERE e.student_id = ? AND e.status = 'ENROLLED'
        """;

    public static final String UPDATE_ENROLLMENT_GRADE = """
        UPDATE enrollments SET grade = ? WHERE student_id = ? AND course_code = ?
        """;

    public static final String DELETE_ENROLLMENT = """
        DELETE FROM enrollments WHERE student_id = ? AND course_code = ?
        """;

    // LIBRARY QUERIES
    public static final String INSERT_BOOK = """
        INSERT INTO library_books (title, author, isbn, publication_year) 
        VALUES (?, ?, ?, ?)
        """;

    public static final String SELECT_BOOKS_BY_TITLE = """
        SELECT * FROM library_books WHERE title LIKE ? AND is_borrowed = FALSE
        """;

    public static final String BORROW_BOOK = """
        UPDATE library_books SET is_borrowed = TRUE WHERE book_id = ?
        """;

    public static final String RETURN_BOOK = """
        UPDATE library_books SET is_borrowed = FALSE WHERE book_id = ?
        """;

    // PAYMENT QUERIES
    public static final String INSERT_PAYMENT = """
        INSERT INTO payments (payer_id, payee_id, amount, payment_type, description) 
        VALUES (?, ?, ?, ?, ?)
        """;

    public static final String SELECT_PAYMENTS_BY_PERSON = """
        SELECT * FROM payments WHERE payer_id = ? OR payee_id = ? ORDER BY payment_date DESC
        """;

    // JOIN QUERIES (for reports)
    public static final String STUDENT_COURSES_JOIN = """
        SELECT s.name AS student_name, c.title AS course_title, e.grade, e.enrollment_date 
        FROM enrollments e 
        JOIN students s ON e.student_id = s.student_id 
        JOIN courses c ON e.course_code = c.course_code 
        WHERE e.grade IS NOT NULL
        ORDER BY s.name
        """;

    public static final String FACULTY_DEPARTMENT_JOIN = """
        SELECT f.faculty_id, p.name, d.name AS department_name, f.salary 
        FROM faculty f 
        JOIN persons p ON f.person_id = p.person_id 
        JOIN department_faculty df ON f.faculty_id = df.faculty_id 
        JOIN departments d ON df.dept_id = d.dept_id
        """;
}