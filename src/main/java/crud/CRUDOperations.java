package crud;

import pojo.Customer;

import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class CRUDOperations {

    private Scanner scanner = new Scanner(System.in);


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
            //preparedStatement.executeUpdate(); //not necessary now because "int changedRows = preparedStatement.executeUpdate();"

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

    //SELECT

    public void select (Connection c, String text) throws SQLException {

        String query2 = "select * \r\n"
                + "from customer\r\n"
                + "where first_name LIKE ? or last_name LIKE ?";
        try(PreparedStatement preparedStatement = c.prepareStatement(query2,
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY)){

            preparedStatement.setString(1, "%" + text + "%");
            preparedStatement.setString(2, "%" + text + "%");

            ResultSet resultSet = preparedStatement.executeQuery();
            //System.out.println("rs_row: "+resultSet.getRow());
            resultSet.beforeFirst();
            if(resultSet.next()){

                System.out.println("The first_name or last_name you are looking for is based on the entry found:");
                resultSet.beforeFirst();

                while (resultSet.next()) {
                    int customerId = resultSet.getInt("customer_id");
                    String firstName = resultSet.getString("first_name");
                    String lastName = resultSet.getString("last_name");
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");
                    LocalDate dateOfBirth = resultSet.getDate("date_of_birth").toLocalDate();
                    boolean active = resultSet.getBoolean("active");
                    String address = resultSet.getString("address");
                    Customer customer = new Customer(customerId, firstName, lastName, email, password, dateOfBirth, active, address);
                    //System.out.println();
                    System.out.println(customer);

                }
            } else {
                System.out.println("No such first_name or last_name in the list!");
            }

        }catch(SQLException e) {
            System.err.println("Error code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            System.err.println("State: " + e.getSQLState());
            e.printStackTrace();
        }
    }

    //DELETE
    //public void deleteCustomer (Connection connection) {
    public void deleteCustomer (Connection connection, String text) {
        //String deletedCustomer = "DELETE from customer WHERE first_name =? or  last_name =?";
        String deletedCustomer = "DELETE from customer WHERE first_name =?";

        try(PreparedStatement statement = connection.prepareStatement(deletedCustomer)){

            //System.out.print("first_name or last_name?: ");
            //String input = scanner.nextLine();

            statement.setString(1, text);
            //statement.setString(2, input);

            int changedRows = statement.executeUpdate();
            System.out.println("Deleted rows: " + changedRows);
            if(changedRows !=0){
                System.out.println("Customer table recreated");
                System.out.println("Customer '" + text + "' has been deleted from database!");
            } else {
                System.out.println("Customer '" + text + "' has not been deleted from database, or does not exist!");
            }


        }catch(SQLException e) {
            System.err.println("Error occurred when executing SQL statement");
            System.err.println("Error code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            System.err.println("State: " + e.getSQLState());
            //System.err.println("State: " + e.getLocalizedMessage());
        }
    }


}
