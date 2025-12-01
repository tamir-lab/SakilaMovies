package com.pluralsight;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Application needs two arguments to run: " +
                    "java com.pluralsight.Main <username> <password>");
            System.exit(1);
        }
        String username = args[0];
        String password = args[1];
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/sakila");
        dataSource.setUsername(username);
        dataSource.setPassword(password);


        Scanner scanner = new Scanner(System.in);
        System.out.println("Search actor by last name:");
        String lastNameToSearch = scanner.nextLine();
        displayActorByLastName(dataSource,lastNameToSearch);
        System.out.println("To search for a movie by an actor");
        System.out.println("Enter the first name:");
        String firstNameToSearch = scanner.nextLine();

        System.out.println("Enter the last name:");
        lastNameToSearch = scanner.nextLine();

        displayMovieByActorName(dataSource,lastNameToSearch,firstNameToSearch);



    }
    public static void displayActorByLastName(BasicDataSource dataSource, String lastNameToSearch) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT first_name, last_name FROM actor WHERE last_name = ?"
             )) {
            statement.setString(1, lastNameToSearch);
            try(ResultSet results = statement.executeQuery()) {
                if (results.next()){
                    System.out.println("Your matches are: \n");
                    do {
                        String firstName = results.getString("first_name");
                        String lastName = results.getString("last_name");
                        System.out.println(firstName + " " + lastName);
                    } while (results.next());
                }

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void displayMovieByActorName(BasicDataSource dataSource, String lastNameToSearch, String firstNameToSearch) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT f.title " +
                             "FROM actor a " +
                             "JOIN film_actor fa ON a.actor_id = fa.actor_id " +
                             "JOIN film f ON f.film_id = fa.film_id " +
                             "WHERE first_name = ? AND last_name = ?"
             )) {
            statement.setString(1,firstNameToSearch);
            statement.setString(2,lastNameToSearch);
            try(ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    System.out.println("Your matches are: \n");
                    do {
                        String title = results.getString("title");
                        System.out.println(title);
                    } while(results.next());
                } else {
                    System.out.println("No matches");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
