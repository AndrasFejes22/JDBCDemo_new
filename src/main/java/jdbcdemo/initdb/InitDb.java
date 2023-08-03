package jdbcdemo.initdb;

import org.apache.commons.lang3.StringUtils;
import pojo.Customer;
import pojo.Order;
import pojo.Product;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    //private final static Random random = new Random(8735432L);
    private final static Random random = new Random();
    public static void main(String[] args) {
        new InitDb().run();
    }

    private void run() {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/webshop", "postgres", "admin")) {
            truncateTablesIfNecessary(connection);
            populateCustomerTable(connection, 20);
            populateProductTable(connection);
            populateOrdersTable(connection, 20);
        } catch (SQLException e) {
            System.out.println("Error while uploading the database!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error while opening file!");
            e.printStackTrace();
        }
        System.out.println("The programme has been run.");
    }

    private void populateOrdersTable(Connection connection, int amount) throws SQLException {
        // IDs:
        List<Integer> customerIds = loadCustomerIds(connection);
        List<Integer> productIds = loadProductIds(connection);

        // create Customer objects
        List<Order> orderList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            orderList.add(new Order(0, getRandomFrom(customerIds), getRandomFrom(productIds)));
        }

        // populate the database
        // INSERT sql:
        String sql = """
                        INSERT INTO public.orders(
                        	order_id, customer_id, product_id)
                        	VALUES (nextval('orders_seq'), ?, ?);
                        """;
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            for (Order order : orderList) {
                preparedStatement.setInt(1, order.getCustomerId());
                preparedStatement.setInt(2, order.getProductId());


                preparedStatement.executeUpdate();
            }
        }

    }

    private List<Integer> loadCustomerIds(Connection connection) throws SQLException {
        List<Integer> customerIds = new ArrayList<>();
        String sql = "SELECT customer_id FROM customer";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()){
                    int customerId = resultSet.getInt("customer_id");
                    customerIds.add(customerId);
                }
            }
        }
        return customerIds;
    }

    private List<Integer> loadProductIds(Connection connection) throws SQLException{
        List<Integer> productIds = new ArrayList<>();
        String sql = "SELECT product_id FROM product";
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()){
                    int productId = resultSet.getInt("product_id");
                    productIds.add(productId);
                }
            }
        }
        return productIds;
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

    private void populateProductTable(Connection connection) throws IOException, SQLException {
        List<Product> productList = new ArrayList<>();
        File file = new File("src/main/resources/PCParts.txt");
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while((line = br.readLine()) != null){
            String[] arr = line.split(" ");
            String name = arr[0];
            String description = arr[0];
            int unit_price = Integer.parseInt(arr[1]);
            int stock_quantity = random.nextInt(50);
            Product product = new Product(0, name, description, unit_price, stock_quantity);
            productList.add(product);

        }
        //System.out.println(productList); // check

        // populate the database
        // INSERT sql:
        String sql = """
                        INSERT INTO public.product(
                        	product_id, name, description, unit_price, stock_quantity)
                        	VALUES (nextval('product_seq'), ?, ?, ?, ?);
                        """;
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            for (Product product : productList) {
                preparedStatement.setString(1, product.getName());
                preparedStatement.setString(2, product.getDescription());
                preparedStatement.setInt(3, product.getUnitPrice());
                preparedStatement.setInt(4, product.getQuantity());

                preparedStatement.executeUpdate();
            }
        }

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
        List<String> mailProviders = Files.lines(Paths.get("src/main/resources/mailProviders.txt")).toList();

        // create Customer objects
        List<Customer> customerList = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            String firstName = getRandomFrom(firstNames);
            String lastName = getRandomFrom(lastNames);
            String email = removeAccentsWithApacheCommons(firstName.toLowerCase()) + "." + removeAccentsWithApacheCommons(lastName.toLowerCase()) + getRandomFrom(mailProviders);
            String password = passwordGenerator();
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
        //customerList.forEach(System.out::println); // check

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

    public static <T> T getRandomFrom(List<T> list){
        return list.get(random.nextInt(list.size()));
    }

    public static String getRandomFromList(List<String> list){
        Random randomNumber = new Random();
        return list.get(randomNumber.nextInt(list.size()));
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

        int minLength = 8;
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < minLength/4; i++) {
            stringBuilder.append(getRandomFromList(capitalLetters));
            stringBuilder.append(getRandomFromList(symbols));
            stringBuilder.append(getRandomFromList(numbers));
            stringBuilder.append(getRandomFromList(capitalLetters).toLowerCase());
        }
        //shuffle:

        String temporaryPassword = stringBuilder.toString();

        //System.out.println(stringBuilder.toString().length());
        return shuffleString(temporaryPassword);
    }

    public static String shuffleString(String input){
        List<String> letters = Arrays.asList(input.split(""));
        Collections.shuffle(letters);
        return StringUtils.join(letters,"");
    }

}
