package jdbcdemo.initdb;

import pojo.Customer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Write a Java program that randomly fills the tables of the "webshop" database with sample data.
 * The program should be parameterizable (e.g. how many records are inserted into which table.)
 * TODO
 * passwordGenerator();
 * dateOfBirthGenerator();
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
        // amegadott file-nak a sorait adja vissza String-kent:
        //Files.lines(Paths.get("src/main/resources/vezeteknevek.txt")).forEach(System.out::println);

        List<String> lastNames = Files.lines(Paths.get("src/main/resources/vezeteknevek.txt")).toList();
        List<String> firstNames = Files.lines(Paths.get("src/main/resources/keresztnevek.txt")).toList();
        List<String> cities = Files.lines(Paths.get("src/main/resources/varosok.txt")).toList();
        List<String> streetTypes = Files.lines(Paths.get("src/main/resources/utcanevek.txt")).toList();
        List<String> namesOfFamousPeople = Files.lines(Paths.get("src/main/resources/hires_emberek.txt")).toList();

        // create Customer objects
        List<Customer> customerList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            String firstName = getRandomFrom(firstNames);
            String lastName = getRandomFrom(lastNames);
            String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@gmail.com";
            String password = "abc123"; // passwordGenerator();
            LocalDate dateOfBirth = dateOfBirthGenerator();
            boolean active = random.nextBoolean();
            String address = getRandomFrom(cities) + " "
                    + (random.nextInt(9000) + 1000) + " "
                    + getRandomFrom(namesOfFamousPeople) + " "
                    + getRandomFrom(streetTypes) + " "
                    + (random.nextInt((100) + 1)) + ".";
            Customer customer = new Customer(0, lastName, firstName, email, password, dateOfBirth, active, address);
            customerList.add(customer);
        }
        System.out.println("Customers list: ");
        customerList.forEach(System.out::println);
        // populate the database
    }

    public static List<String> mergeTwoList(List<String> list1, List<String> list2){
        return Stream.of(list1, list2).flatMap(l -> l.stream()).toList();
    }

    private String getRandomFrom(List<String> list){
        return list.get(random.nextInt(list.size()));
    }

    public static LocalDate dateOfBirthGenerator(){
        Random myRandom = new Random();
        String nullNumber = "0";
        int year = myRandom.nextInt(1900, LocalDate.now().getYear()-18);
        int month = myRandom.nextInt(12) + 1;
        String monthString = String.valueOf(month);
        if(monthString.length() == 1){
            monthString = nullNumber.concat(monthString);
        }
        int day = myRandom.nextInt(28) + 1;
        String dayString = String.valueOf(day);
        if(dayString.length() == 1){
            dayString = nullNumber.concat(dayString);
        }
        String date = String.valueOf(year) + "-" + String.valueOf(monthString) + "-" + String.valueOf(dayString);
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
