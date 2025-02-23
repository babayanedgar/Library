package utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbConnection {
    private static final String connectionUrl =
            "jdbc:sqlserver://localhost:1433;"
                    + "database=Library;"
                    + "user=root;"
                    + "password=root;"
                    + "encrypt=true;"
                    + "trustServerCertificate=true;"
                    + "loginTimeout=30;";

    public static Connection getConnection() throws SQLException {
        try {
            // Ensure the driver is loaded
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBC Driver not found! Make sure it is added to the classpath.", e);
        }
        return DriverManager.getConnection(connectionUrl);
    }
}
