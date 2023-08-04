package crud;

import pojo.Customer;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Crud {

    //INSERT
    public void insertPerson (Connection connection, Customer customer) {
        String sql = """
                        INSERT INTO public.customer(
                        	customer_id, first_name, last_name, email, password, date_of_birth, active, address)
                        	VALUES (nextval('customer_seq'), ?, ?, ?, ?, ?, ?, ?);
                        """;
        try(PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setString(1, customer.getFirstName());
            preparedStatement.setString(2, customer.getLastName());
            preparedStatement.setString(3, customer.getEmail());
            preparedStatement.setString(4, customer.getPassword());
            preparedStatement.setDate(5, Date.valueOf(customer.getDateOfBirth()));
            preparedStatement.setBoolean(6, customer.isActive());
            preparedStatement.setString(7, customer.getAddress());
            preparedStatement.executeUpdate();

            int changedRows = preparedStatement.executeUpdate();
            System.out.println("Modified rows: " + changedRows);

            System.out.println("Posts table recreated");
        }catch(SQLException e) {
            System.err.println("Error occurred when executing SQL statement");
            System.err.println("Error code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            System.err.println("State: " + e.getSQLState());
            //System.err.println("State: " + e.getLocalizedMessage());
        }
    }
}
