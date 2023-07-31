package jdbcdemo;

import pojo.Customer;

import java.sql.*;
import java.time.LocalDate;

public class JDBCSelectDemo {
    public static void main(String[] args) throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/webshop", "postgres","admin")) {

            // SELECT
            String sql = """
                    SELECT customer_id, first_name, last_name, email, password, date_of_birth, active
                    	FROM public.customer;
                    """;

            String sqlWhere = """
                    SELECT customer_id, first_name, last_name, email, password, date_of_birth, active
                    	FROM public.customer WHERE customer_id = 2;
                    """;

            // 2. Statement object
            try(Statement statement = connection.createStatement()){
                // 3. execute query
                try(ResultSet resultSet = statement.executeQuery(sql)){
                    while (resultSet.next()){
                        int customerId = resultSet.getInt("customer_id");
                        String firstName = resultSet.getString("first_name");
                        String lastName = resultSet.getString("first_name");
                        String email = resultSet.getString("first_name");
                        String password = resultSet.getString("first_name");
                        LocalDate dateOfBirth = resultSet.getDate("date_of_birth").toLocalDate();
                        boolean active = resultSet.getBoolean("active");
                        Customer customer = new Customer(customerId, firstName, lastName, email, password, dateOfBirth, active);
                        //System.out.println();
                        System.out.println(customer);
                    }
                }
            }

        }
        System.out.println("Connection successful");
    }
}
