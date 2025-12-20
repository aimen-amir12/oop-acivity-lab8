package university.database;

import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/university_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Java123.";
    private static Connection connection = null;
    private static boolean initialized = false;

    // Private constructor to prevent instantiation
    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {connection = DriverManager.getConnection(URL, USER, PASSWORD);
                if (!initialized) {
                    System.out.println(" Database connected successfully!");
                    initialized = true;
                }
            } catch (SQLException e) {
                throw new SQLException("MySQL JDBC Driver not found", e);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

    public static boolean testConnection() {
        try {
            getConnection();
            return true;
        } catch (SQLException e) {
            System.out.println(" Connection failed: " + e.getMessage());
            System.out.println("URL: " + URL);
            System.out.println("User: " + USER);
            return false;
        }
    }
}