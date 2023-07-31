package jdbcdemo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class JDBC_SQLInjectionDemo {
    public static void main(String[] args) throws SQLException {
        // User interaction:
        try(Scanner scanner = new Scanner(System.in)){

            System.out.print("Kérem adja meg a keresztnevet: ");
            String firstName = scanner.nextLine();
            System.out.print("Kérem adja meg a vezetéknevet: ");
            String lastName = scanner.nextLine();
            System.out.print("Kérem adja meg az e-mail címet: ");
            String email = scanner.nextLine();
            System.out.print("Kérem adja meg a jelszót: ");
            String password = scanner.nextLine();
            System.out.print("Kérem adja meg a születési dátumot (YYYY-MM-DD): ");
            String dateOfBirth = scanner.nextLine();

        }
        // 1. Connection object:
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/webshop", "postgres","admin")) {
            //String sql = "SELECT * FROM public.customer";
            String sql = """
                    INSERT INTO public.customer(
                    	customer_id, first_name, last_name, email, password, date_of_birth, active)
                    	VALUES (nextval('customer_seq'), 'JDBC3', 'test3', 'jdbc3@gmail.com', '123ABCDE', '1999-01-23', true);
                    """;
            // 2. Statement object
            try(Statement statement = connection.createStatement()){
                // 3. execute query
                statement.executeUpdate(sql);
            }

        }
        System.out.println("Connection successful");
    }
}