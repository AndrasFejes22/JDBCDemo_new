package jdbcdemo;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class JDBC_SQLInjectionPreventionDemo {
    public static void main(String[] args) throws SQLException {
        // User interaction:
        try (Scanner scanner = new Scanner(System.in)) {

            System.out.print("Kérem adja meg a keresztnevet: ");
            String firstName = scanner.nextLine();
            System.out.print("Kérem adja meg a vezetéknevet: ");
            String lastName = scanner.nextLine();
            System.out.print("Kérem adja meg az e-mail címet: ");
            String email = scanner.nextLine();
            System.out.print("Kérem adja meg a jelszót: ");
            String password = scanner.nextLine();
            System.out.print("Kérem adja meg a születési dátumot (YYYY-MM-dd): ");
            String dateOfBirth = scanner.nextLine();


            // 1. Connection object:
            try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/webshop", "postgres", "admin")) {

                // String manipulation + statement: SQL injection danger! --> Solving: PreparedStatement
                String sql = """
                        INSERT INTO public.customer(
                        	customer_id, first_name, last_name, email, password, date_of_birth, active)
                        	VALUES (nextval('customer_seq'), ?, ?, ?, ?, ?, true);
                        """;
                // 2. PreparedStatement object
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    // 3. parameters
                    preparedStatement.setString(1, firstName);
                    preparedStatement.setString(2, lastName);
                    preparedStatement.setString(3, email);
                    preparedStatement.setString(4, password);
                    preparedStatement.setDate(5, Date.valueOf(LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
                    // 4. execute query
                    preparedStatement.executeUpdate();
                }

            }
            System.out.println("Connection successful");
        }
    }
}