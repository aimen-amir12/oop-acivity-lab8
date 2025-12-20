package university.modules;

import university.database.DatabaseConnection;
import university.database.SQLQueries;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class LibraryManager {
    private static Scanner scanner = new Scanner(System.in);

    public static void showMenu() {
        while (true) {
            System.out.println("\n=== LIBRARY MANAGEMENT ===");
            System.out.println("1. Add New Book");
            System.out.println("2. View All Books");
            System.out.println("3. Search Available Books");
            System.out.println("4. Borrow Book");
            System.out.println("5. Return Book");
            System.out.println("6. View Borrowing History");
            System.out.println("7. Calculate Fine");
            System.out.println("8. Back to Main Menu");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: addBook(); break;
                case 2: viewAllBooks(); break;
                case 3: searchBooks(); break;
                case 4: borrowBook(); break;
                case 5: returnBook(); break;
                case 6: viewBorrowingHistory(); break;
                case 7: calculateFine(); break;
                case 8: return;
                default: System.out.println("Invalid choice!");
            }
        }
    }

    private static void addBook() {
        System.out.println("\n=== ADD NEW BOOK ===");

        System.out.print("Book Title: ");
        String title = scanner.nextLine();

        System.out.print("Author: ");
        String author = scanner.nextLine();

        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();

        System.out.print("Publication Year: ");
        int year = scanner.nextInt();
        scanner.nextLine();

        String sql = SQLQueries.INSERT_BOOK;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, isbn);
            pstmt.setInt(4, year);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Book added successfully!");
            } else {
                System.out.println("Failed to add book!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewAllBooks() {
        System.out.println("\n=== ALL BOOKS ===");
        System.out.println("ID\tTitle\t\t\tAuthor\t\t\tStatus");
        System.out.println("----------------------------------------------------------------");

        String sql = "SELECT * FROM library_books ORDER BY book_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String status = rs.getBoolean("is_borrowed") ? "BORROWED" : "AVAILABLE";
                System.out.printf("%-4d\t%-20s\t%-15s\t%s\n",
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        status);
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void searchBooks() {
        System.out.println("\n=== SEARCH AVAILABLE BOOKS ===");
        System.out.print("Enter search term (title/author): ");
        String search = scanner.nextLine();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQLQueries.SELECT_BOOKS_BY_TITLE)) {

            pstmt.setString(1, "%" + search + "%");
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\nAvailable Books:");
            System.out.println("ID\tTitle\t\t\tAuthor");
            System.out.println("----------------------------------------");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-4d\t%-20s\t%s\n",
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"));
            }

            if (!found) {
                System.out.println("No available books found!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void borrowBook() {
        System.out.println("\n=== BORROW BOOK ===");

        System.out.print("Book ID: ");
        int bookId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Borrower ID: ");
        String borrowerId = scanner.nextLine();

        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(14); // 2 weeks loan

        String borrowSql = """
            INSERT INTO library_transactions (book_id, borrower_id, borrow_date, due_date, status) 
            VALUES (?, ?, ?, ?, 'BORROWED')
            """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Insert transaction
            try (PreparedStatement pstmt1 = conn.prepareStatement(borrowSql)) {
                pstmt1.setInt(1, bookId);
                pstmt1.setString(2, borrowerId);
                pstmt1.setDate(3, Date.valueOf(borrowDate));
                pstmt1.setDate(4, Date.valueOf(dueDate));
                pstmt1.executeUpdate();
            }

            // Update book status
            try (PreparedStatement pstmt2 = conn.prepareStatement(SQLQueries.BORROW_BOOK)) {
                pstmt2.setInt(1, bookId);
                pstmt2.executeUpdate();
            }

            conn.commit();
            System.out.println("Book borrowed successfully!");
            System.out.println("Due Date: " + dueDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void returnBook() {
        System.out.println("\n=== RETURN BOOK ===");

        System.out.print("Book ID: ");
        int bookId = scanner.nextInt();
        scanner.nextLine();

        LocalDate returnDate = LocalDate.now();

        String returnSql = """
            UPDATE library_transactions 
            SET return_date = ?, status = 'RETURNED' 
            WHERE book_id = ? AND status = 'BORROWED'
            """;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Update transaction
            try (PreparedStatement pstmt1 = conn.prepareStatement(returnSql)) {
                pstmt1.setDate(1, Date.valueOf(returnDate));
                pstmt1.setInt(2, bookId);
                int rows = pstmt1.executeUpdate();

                if (rows == 0) {
                    System.out.println("Book not found or already returned!");
                    conn.rollback();
                    return;
                }
            }

            // Update book status
            try (PreparedStatement pstmt2 = conn.prepareStatement(SQLQueries.RETURN_BOOK)) {
                pstmt2.setInt(1, bookId);
                pstmt2.executeUpdate();
            }

            conn.commit();
            System.out.println("Book returned successfully!");

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewBorrowingHistory() {
        System.out.println("\n=== BORROWING HISTORY ===");
        System.out.print("Enter Borrower ID (or press Enter for all): ");
        String borrowerId = scanner.nextLine();

        String sql;
        if (borrowerId.isEmpty()) {
            sql = """
                SELECT t.*, b.title, p.name 
                FROM library_transactions t 
                JOIN library_books b ON t.book_id = b.book_id 
                JOIN persons p ON t.borrower_id = p.person_id 
                ORDER BY t.borrow_date DESC
                """;
        } else {
            sql = """
                SELECT t.*, b.title, p.name 
                FROM library_transactions t 
                JOIN library_books b ON t.book_id = b.book_id 
                JOIN persons p ON t.borrower_id = p.person_id 
                WHERE t.borrower_id = ? 
                ORDER BY t.borrow_date DESC
                """;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (!borrowerId.isEmpty()) {
                pstmt.setString(1, borrowerId);
            }

            ResultSet rs = pstmt.executeQuery();

            System.out.println("\nBorrowing History:");
            System.out.println("Book\t\t\tBorrower\tBorrow Date\tDue Date\tReturn Date\tStatus");
            System.out.println("--------------------------------------------------------------------------------");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-15s\t%-10s\t%s\t%s\t%s\t%s\n",
                        rs.getString("title"),
                        rs.getString("name"),
                        rs.getDate("borrow_date"),
                        rs.getDate("due_date"),
                        rs.getDate("return_date") != null ? rs.getDate("return_date") : "N/A",
                        rs.getString("status"));
            }

            if (!found) {
                System.out.println("No borrowing records found!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void calculateFine() {
        System.out.println("\n=== CALCULATE FINE ===");

        String sql = """
            SELECT book_id, borrower_id, due_date, 
                   DATEDIFF(CURDATE(), due_date) as days_late 
            FROM library_transactions 
            WHERE status = 'BORROWED' AND CURDATE() > due_date
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\nOverdue Books:");
            System.out.println("Book ID\tBorrower ID\tDue Date\tDays Late\tFine (Rs.)");
            System.out.println("------------------------------------------------------------");

            double totalFines = 0;
            boolean found = false;

            while (rs.next()) {
                found = true;
                int daysLate = rs.getInt("days_late");
                double fine = daysLate * 10; // Rs. 10 per day
                totalFines += fine;

                System.out.printf("%-7d\t%-12s\t%s\t%-10d\t%.2f\n",
                        rs.getInt("book_id"),
                        rs.getString("borrower_id"),
                        rs.getDate("due_date"),
                        daysLate,
                        fine);
            }

            if (found) {
                System.out.println("------------------------------------------------------------");
                System.out.printf("Total Fines Due: Rs. %.2f\n", totalFines);
            } else {
                System.out.println("No overdue books!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}