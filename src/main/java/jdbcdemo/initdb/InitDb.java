package jdbcdemo.initdb;

import org.apache.commons.lang3.StringUtils;
import pojo.Customer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
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
 * proper e-mail addresses
 */
public class InitDb {

    private final boolean TRUNCATE_TABLES = true;
    private final Random random = new Random(8735432L);
    public static void main(String[] args) {
        new InitDb().run();
    }

    private void run() {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/webshop", "postgres", "admin")) {
            truncateTablesIfNecessary(connection);
            populateCustomerTable(connection, 20);
            //populateProductTable(connection, 20);
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

    private void populateProductTable(Connection connection, int amount) {

    }

    private void populateCustomerTable(Connection connection, int amount) throws IOException, SQLException {
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
            String email = removeAccentsWithApacheCommons(firstName.toLowerCase()) + "." + removeAccentsWithApacheCommons(lastName.toLowerCase()) + "@gmail.com";
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
        //customerList.forEach(System.out::println);
        // populate the database
        // INSERT sql:
        String sql = """
                        INSERT INTO public.customer(
                        	customer_id, first_name, last_name, email, password, date_of_birth, active, address)
                        	VALUES (nextval('customer_seq'), ?, ?, ?, ?, ?, ?, ?);
                        """;

        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            for (Customer customer : customerList) {
                preparedStatement.setString(1, customer.getFirstName());
                preparedStatement.setString(2, customer.getLastName());
                preparedStatement.setString(3, customer.getEmail());
                preparedStatement.setString(4, customer.getPassword());
                preparedStatement.setDate(5, Date.valueOf(customer.getDateOfBirth()));
                preparedStatement.setBoolean(6, customer.isActive());
                preparedStatement.setString(7, customer.getAddress());
                preparedStatement.executeUpdate();
            }
        }
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

    static String removeAccentsWithApacheCommons(String input) {
        return StringUtils.stripAccents(input);
    }

    public static String passwordGenerator(){

        List<String> capitalLetters = List.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");
        List<String> symbols = List.of("#", "&", "!", "@", "%", "$", "{", "}", "n", "(", ")", "?", ":", "+", "-", "/", "*", "_", "|", "^", ",", ";", "~", "'");
        List<String> numbers = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
        return "";

    }

}
