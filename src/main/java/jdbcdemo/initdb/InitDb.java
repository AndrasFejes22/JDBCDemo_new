package jdbcdemo.initdb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

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
        } catch (IOException e) {
            System.out.println("Error while opening file!");
            e.printStackTrace();
        }
        System.out.println("The programme has been run.");
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

    private void populateCustomerTable(Connection connection, int amount) throws IOException {
        // datas from txt
        // amegadott file-nak a sorait adja vissza String-k�nt:
        //Files.lines(Paths.get("src/main/resources/vezeteknevek.txt")).forEach(System.out::println);
        List<String> lastNames = Files.lines(Paths.get("src/main/resources/vezeteknevek.txt")).toList();
        List<String> firstNames = Files.lines(Paths.get("src/main/resources/keresztnevek.txt")).toList();
        // create Customer objects
        // populate the database
    }

    public static List<String> mergeTwoList(List<String> list1, List<String> list2){
        return Stream.of(list1, list2).flatMap(l -> l.stream()).toList();
    }
}
