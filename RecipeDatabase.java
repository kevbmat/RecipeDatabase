import java.util.*;
import java.io.*;
import java.nio.charset.MalformedInputException;
import java.sql.*;
import java.sql.Date;

import com.mysql.jdbc.Driver;

public class RecipeDatabase {
    private static boolean running = true;
    private static String currUser = "";

    public static void main(String[] args) {

        Scanner userinput = new Scanner(System.in);
        Connection dbConn = connectToDB();
        while (running) {
            introScreen(userinput, dbConn);
        }

    }

    public static void introScreen(Scanner sc, Connection conn) {
        System.out.println("Select option below");
        System.out.println("(1) Login to application");
        System.out.println("(2) Create a new account");
        System.out.println("(3) Exit program");

        char input = sc.nextLine().charAt(0);
        switch (input) {
        case '1':
            loginScreen(sc, conn);
            break;
        case '2':
            createAccountScreen(sc, conn);
            break;
        case '3':
            running = false;
            System.out.println("Ending Program :)");
            System.exit(0);
            break;
        default:
            System.out.println("Invalid choice");
        }
    }

    public static void loginScreen(Scanner sc, Connection conn) {
        boolean isNotValidInput = true;

        while (isNotValidInput) {
            Console console = System.console();

            System.out.print("Please enter your username: ");
            String username = console.readLine();

            System.out.print("Please enter your password: ");
            char[] passwordChars = console.readPassword();
            String password = new String(passwordChars);

            if (doesUserExist(conn, username)) {

                if (isValidPassword(conn, password)) {
                    currUser = username;
                    isNotValidInput = false;
                    break;
                } else {
                    System.out.println("Please enter a valid username or password.");
                }

            } else {
                System.out.println("Please enter a valid username or password.");
            }
        }

        System.out.println("YOU ARE IN BOIIIIIII");
    }

    private static boolean doesUserExist(Connection conn, String username) {
        if (username.equals("")) {
            return false;
        }

        try {
            String query = "SELECT * FROM account WHERE username=?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void deleteRecipe(Scanner userinput, Connection conn) {
        boolean isNotValidInput = true;
        int recipeId = 0;

        while (isNotValidInput) {
            System.out.print("Please enter the id of the recipe to be deleted: ");
            try {
                recipeId = userinput.nextInt();
                userinput.nextLine();
                if (isValidRecipeId(recipeId, conn)) {
                    isNotValidInput = false;
                    break;
                } else {
                    System.out.println("Please enter a valid recipe id");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter an integer");
            }
        }

        String query1 = "DELETE FROM recipe_type WHERE recipe_id=?";
        String query2 = "DELETE FROM user_recipes WHERE recipe_id=?";
        String query3 = "DELETE FROM recipe WHERE recipe_id=?";

        try {
            PreparedStatement stmt1 = conn.prepareStatement(query1);
            stmt1.setInt(1, recipeId);
            stmt1.execute();

            PreparedStatement stmt2 = conn.prepareStatement(query2);
            stmt2.setInt(1, recipeId);
            stmt2.execute();

            PreparedStatement stmt3 = conn.prepareStatement(query3);
            stmt3.setInt(1, recipeId);
            stmt3.execute();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error in deleting recipe");
        }

    }

    private static boolean isValidRecipeId(int id, Connection conn) {
        if (id <= 0) {
            return false;
        }

        try {
            String query = "SELECT * FROM recipe WHERE recipe_id=?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }

    private static boolean isValidPassword(Connection conn, String password) {
        if (password.equals("")) {
            return false;
        }

        try {
            String query = "SELECT * FROM passwords WHERE password=?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void createAccountScreen(Scanner sc, Connection conn) {

        boolean isNotValidInput = true;
        String username = "";
        String password1 = "";
        String email = "";
        String country = "";
        String dob = "";

        while (isNotValidInput) {
            Console console = System.console();

            System.out.println("ACCOUNT CREATION:");
            System.out.print("Enter username: ");
            username = console.readLine();
            if (!doesUserExist(conn, username)) {

                System.out.print("Enter password: ");
                char[] passwordChars1 = console.readPassword();
                password1 = new String(passwordChars1);
                Arrays.fill(passwordChars1, ' ');

                System.out.print("Enter your password again: ");
                char[] passwordChars2 = console.readPassword();
                String password2 = new String(passwordChars2);
                Arrays.fill(passwordChars2, ' ');

                if (password1.equals(password2)) {
                    System.out.print("Enter email: ");
                    email = console.readLine();

                    System.out.print("Enter DOB (yyyy-mm-dd): ");
                    dob = console.readLine();

                    System.out.print("Enter country residence: ");
                    country = console.readLine();

                    if ((username.equals("") || password1.equals("") || email.equals("") || country.equals("")
                            || dob.equals(""))
                            || (username.length() > 30 || password1.length() > 30 || country.length() > 2)) {
                        System.out.println("Invalid input. Please enter valid input.");
                    } else {
                        isNotValidInput = false;
                        break;
                    }

                } else {
                    System.out.println("Your passwords do not match! Try again!");
                }

            } else {
                System.out.println("That user already exists! Try again!");
            }
        }

        // insert into account database and password database
        String accountInsert = "INSERT INTO account VALUES(?,?,?,?)";
        String passwordInsert = "INSERT INTO passwords VALUES(?,?)";
        try {
            PreparedStatement stmt1 = conn.prepareStatement(accountInsert);
            stmt1.setString(1, username);
            stmt1.setString(2, email);
            stmt1.setString(3, dob);
            stmt1.setString(4, country);
            stmt1.execute();

            PreparedStatement stmt2 = conn.prepareStatement(passwordInsert);
            stmt2.setString(1, username);
            stmt2.setString(2, password1);
            stmt2.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static Connection connectToDB() {
        boolean isNotValidInput = true;

        while (isNotValidInput) {
            try {
                Console console = System.console();

                // connect to the database (change per user)
                String host = "cps-database.gonzaga.edu";

                System.out.println("Enter username: ");
                String username = console.readLine();

                System.out.println("Enter password");
                char[] passwordChars = console.readPassword();
                String password = new String(passwordChars);

                String dab = "cpsc321_groupC_DB";
                String url = "jdbc:mysql://" + host + "/" + dab;

                Connection conn = DriverManager.getConnection(url, username, password);

                Arrays.fill(passwordChars, ' ');
                // if we get here we made connection
                isNotValidInput = false;
                return conn;

            } catch (Exception e) {
                System.err.println("ERROR: Connection failed");
                System.out.println();
            }

        }
        return null;
    }
}