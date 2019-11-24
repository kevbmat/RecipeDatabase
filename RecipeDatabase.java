import java.util.*;
import java.io.*;
import java.sql.*;
import com.mysql.jdbc.Driver;

public class RecipeDatabase {
    private static boolean running = true;

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

        while (isNotValidInput) {
            Console console = System.console();

            System.out.print("Please enter your username: ");
            String username = console.readLine();

            System.out.print("Please enter your password: ");
            char[] passwordChars = console.readPassword();
            String password = new String(passwordChars);

            if (doesUserExist(conn, username)) {

                if (isValidPassword(conn, password)) {
                    isNotValidInput = false;
                    break;
                } else {
                    System.out.println("Please enter a valid username or password.");
                }

            } else {
                System.out.println("Please enter a valid username or password.");
            }
        }

        System.out.println("ACCOUNT CREATION:");
        System.out.println("Enter username: ");
        String username = sc.next();
        System.out.println("Enter password: ");
        String password = sc.next();
        System.out.println("Enter email: ");
        String email = sc.next();
        System.out.println("Enter DOB: ");
        String dateOfBirth = sc.next();
        System.out.println("Enter country residence: ");
        String country = sc.next();
        System.out.println(username + password + email + dateOfBirth + country);
        // insert into account database and password database
        String query = "INSERT INTO account(username, email, dob, country_residence)";
    }

    public static void createRecipeScreen() {
        System.out.println("CREATE NEW RECIPE");
        System.out.print("Enter")
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