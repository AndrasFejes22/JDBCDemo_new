package jdbcdemo.initdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

/**
 * Write a Java program that randomly fills the tables of the "webshop" database with sample data.
 * The program should be parameterizable (e.g. how many records are inserted into which table.)
 */
public class InitDb {

    private final boolean TRUNCATE_TABLES = false;
    private final Random random = new Random(8735432L);
    public static void main(String[] args) {
        new InitDb().run();
    }

    private void run() {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/webshop", "postgres", "admin")) {
            truncateTablesIfNecessary(connection);
            populateCustomerTable(connection, 20);
        } catch (SQLException e) {
            System.out.println("Error while uploading the database!");
            e.printStackTrace();
        }
        System.out.println("A program lefutott.");
    }

    private void truncateTablesIfNecessary(Connection connection) throws SQLException {
        if(TRUNCATE_TABLES){
            try(Statement truncateStatement = connection.createStatement()){
                truncateStatement.executeUpdate("TRUNCATE table customer CASCADE");
                truncateStatement.executeUpdate("TRUNCATE table product CASCADE");
                truncateStatement.executeUpdate("TRUNCATE table orders CASCADE"); //public.order
            }
        }
    }

    private void populateCustomerTable(Connection connection, int amount){
        // datas from txt
        // create Customer objects
        // populate the database
    }
}
