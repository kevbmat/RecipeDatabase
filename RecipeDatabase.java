import java.util.*;
import java.io.*;
import java.sql.*;
import com.mysql.jdbc.Driver;

public class RecipeDatabase {
    public static void main(String[] args) {
        try {
            Properties prop = new Properties();
            FileInputStream in = new FileInputStream("config.properties");
            prop.load(in);
            in.close();

            // connect to the database (change per user)
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter Database Credentials");
            String host = "tcp:\\cps-database.gonzaga.edu";
            System.out.println("Enter username: ");
            String username = sc.next();
            System.out.println("Enter password");
            String password = sc.next();
            String hst = prop.getProperty(host);
            String user = prop.getProperty(username);
            String pwd = prop.getProperty(password);
            String dab = user + "DB";
            String url = "jdbc:mysql://" + host + "/" + dab;
            Connection conn = DriverManager.getConnection(url, user, pwd);

        } catch (Exception e) {
            System.err.println("Connection failed");
        }
        introScreen(); 
    }

    public static void introScreen() {
        System.out.println("Select option below");
        System.out.println("(1) Login to application");
        System.out.println("(2) Create a new account");
        Scanner sc = new Scanner(System.in);
        char input = sc.nextLine().charAt(0);
        switch (input) {
            case '1':
                break;
            case '2':
                createAccountScreen();
                break;
            default:
                System.out.println("Invalid choice");
                loginScreen();
        }
    }

    public static void loginScreen() {

    }

    public static void createAccountScreen() {
        Scanner sc = new Scanner(System.in);
        System.out.println("ACCOUNT CREATION");
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
        String query = "INSERT INTO account(username, email, dob, country_residence";

    }
}