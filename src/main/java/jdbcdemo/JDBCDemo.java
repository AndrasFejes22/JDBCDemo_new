package jdbcdemo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCDemo {
    public static void main(String[] args) throws SQLException {
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