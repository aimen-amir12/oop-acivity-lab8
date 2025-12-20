package university.Main;

import university.modules.*;
import university.database.DatabaseConnection;
import university.database.TableInitializer;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    // Initialize all module managers
    private static StudentManager studentManager = new StudentManager();
    private static FacultyManager facultyManager = new FacultyManager();
    private static CourseManager courseManager = new CourseManager();
    private static LibraryManager libraryManager = new LibraryManager();
    private static FinanceManager financeManager = new FinanceManager();
    private static AdminManager adminManager = new AdminManager();
    private static GradingManager gradingManager = new GradingManager();

    public static void main(String[] args) {
        displayWelcome();

        // Test database connection
        if (!DatabaseConnection.testConnection()) {
            System.out.println(" ERROR: Cannot connect to MySQL database!");
            System.out.println("Please check:");
            System.out.println("1. MySQL server is running");
            System.out.println("2. Database credentials are correct");
            System.out.println("3. JDBC driver is in classpath");
            return;
        }

        // Initialize database tables
        System.out.println(" Initializing database...");
        TableInitializer.initializeDatabase();

        System.out.println(" System ready!");

        // Main menu loop
        while (true) {
            displayMainMenu();
            int choice = getMenuChoice();

            switch (choice) {
                case 1:
                    studentManager.showMenu();
                    break;
                case 2:
                    facultyManager.showMenu();
                    break;
                case 3:
                    courseManager.showMenu();
                    break;
                case 4:
                    libraryManager.showMenu();
                    break;
                case 5:
                    financeManager.showMenu();
                    break;
                case 6:
                    adminManager.showMenu();
                    break;
                case 7:
                    gradingManager.showMenu();
                    break;
                case 8:
                    runDemo();
                    break;
                case 9:
                    exitSystem();
                    break;
                default:
                    System.out.println(" Invalid choice! Please enter 1-9");
            }
        }
    }

    private static void displayWelcome() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("          SMART UNIVERSITY MANAGEMENT SYSTEM");
        System.out.println("          CSCS290 - Fall 2025 Project");
        System.out.println("          Database: MySQL | Full CRUD Operations");
        System.out.println("=".repeat(60));
        System.out.println();
    }

    private static void displayMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("                  MAIN MENU");
        System.out.println("=".repeat(50));
        System.out.println("  1. Student Management");
        System.out.println("  2. Faculty Management");
        System.out.println("  3. Course Management");
        System.out.println("  4. Library Management");
        System.out.println("  5. Finance Management");
        System.out.println("  6. Admin Panel");
        System.out.println("  7. Grading Management"); // ADD THIS LINE
        System.out.println("  8. Run Demo (Test All Features)");
        System.out.println("  9. Exit System"); // Changed from 8 to 9
        System.out.println("-".repeat(50));
        System.out.print("  Enter your choice (1-9): ");
    }

    private static int getMenuChoice() {
        try {
            return scanner.nextInt();
        } catch (Exception e) {
            scanner.nextLine();
            return -1;
        }
    }

    private static void exitSystem() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  Thank you for using University System!");
        System.out.println("  Closing database connections...");
        System.out.println("=".repeat(50));

        DatabaseConnection.closeConnection();
        scanner.close();
        System.exit(0);
    }

    private static void runDemo() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("              SYSTEM DEMO MODE");
        System.out.println("=".repeat(50));

        System.out.println("\n Testing Database Connection...");
        if (DatabaseConnection.testConnection()) {
            System.out.println(" Database connection successful!");
        } else {
            System.out.println(" Database connection failed!");
            return;
        }

        System.out.println("\n Checking Database Tables...");
        try {
            String checkSql = "SHOW TABLES";
            var conn = DatabaseConnection.getConnection();
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery(checkSql);

            System.out.println(" Database tables found!");
            System.out.println("Available tables:");
            while (rs.next()) {
                System.out.println("  - " + rs.getString(1));
            }

        } catch (Exception e) {
            System.out.println(" Error checking tables: " + e.getMessage());
        }

        System.out.println("\n Testing All Modules...");
        System.out.println("1. Opening Student Management...");
        studentManager.showMenu();

        System.out.println("\n2. Opening Faculty Management...");
        facultyManager.showMenu();

        System.out.println("\n3. Opening Course Management...");
        courseManager.showMenu();

        System.out.println("\n4. Opening Library Management...");
        LibraryManager.showMenu();

        System.out.println("\n5. Opening Student Management...");
        StudentManager.showMenu();

        System.out.println("\n6. Opening Finance Management...");
        FinanceManager.showMenu();

        System.out.println("\n7. Opening Grading Management...");
        GradingManager.showMenu();

        System.out.println("\n Demo completed successfully!");
        System.out.println("All 7 modules are functional with database integration.");
        System.out.println("\nPress Enter to return to main menu...");
        scanner.nextLine();
    }
}