import java.util.*;
import java.io.*;
import java.nio.charset.MalformedInputException;
import java.sql.*;
import java.sql.Date;

import com.mysql.jdbc.Driver;

public class RecipeDatabase {
    private static String currUser = "";

    public static void main(String[] args) {

        Scanner userinput = new Scanner(System.in);
        Connection dbConn = connectToDB();

        boolean inIntroMenu = true;
        while (inIntroMenu) {
            int result = introScreen(userinput, dbConn);
            if (result == 1) {
                inIntroMenu = false;
                break;
            } else if (result == 3) {
                System.out.println("Ending Program :)");
                System.exit(0);
            }
        }

        mainMenu(userinput, dbConn);
    }

    public static int introScreen(Scanner sc, Connection conn) {
        System.out.println("Select option below");
        System.out.println("(1) Login to application");
        System.out.println("(2) Create a new account");
        System.out.println("(3) Exit program");

        char input = sc.nextLine().charAt(0);
        switch (input) {
        case '1':
            System.out.println();
            loginScreen(sc, conn);
            System.out.println();
            return 1;
        case '2':
            System.out.println();
            createAccountScreen(sc, conn);
            return 2;
        case '3':
            return 3;
        default:
            System.out.println("Invalid choice");
        }
        // just need this so java doesnt complain
        return -1;
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
    }

    private static void mainMenu(Scanner sc, Connection conn) {
        boolean inMainMenu = true;
        System.out.println("Welcome " + currUser + "!");

        while (inMainMenu) {
            System.out.println("Select option below");
            System.out.println("(1) Recipes");
            System.out.println("(2) Social");
            System.out.println("(3) Exit");

            char input = sc.nextLine().charAt(0);
            switch (input) {
            case '1':
                System.out.println();
                recipeMenu(sc, conn);
                break;
            case '2':
                System.out.println();
                // socialMenu(sc, conn);
                break;
            case '3':
                inMainMenu = false;
                System.out.println("Ending Program :)");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice");
            }
        }
    }

    private static void recipeMenu(Scanner sc, Connection conn) {
        boolean inRecipeMenu = true;

        while (inRecipeMenu) {
            System.out.println("Select option below");
            System.out.println("(1) View Recipes");
            System.out.println("(2) Add Recipe");
            System.out.println("(3) Update Recipe");
            System.out.println("(4) Delete Recipe");
            System.out.println("(5) Return to main menu");

            char input = sc.nextLine().charAt(0);
            switch (input) {
            case '1':
                System.out.println();
                listRecipes(conn);
                break;
            case '2':
                System.out.println();
                createRecipeScreen(conn);
                break;
            case '3':
                createRecipeScreen(conn);
                System.out.println();
                break;
            case '4':
                System.out.println();

                break;
            case '5':
                inRecipeMenu = false;
                System.out.println();
                break;
            default:
                System.out.println("Invalid choice");
            }
        }
    }

    private static void socialMenu(Scanner sc, Connection conn) {
        boolean inSocialMenu = true;

        while (inSocialMenu) {
            System.out.println("Select option below");
            System.out.println("(1) News Feed");
            System.out.println("(2) View Trending Users");
            System.out.println("(3) Follow Someone");
            System.out.println("(4) Unfollow Someone");

            char input = sc.nextLine().charAt(0);
            switch (input) {
            case '1':
                System.out.println();
                listNewsFeed(conn);
                break;
            case '2':
                System.out.println();

                break;
            case '3':
                System.out.println();

                break;
            case '4':
                System.out.println();

                break;
            case '5':
                inSocialMenu = false;
                System.out.println();
                break;
            default:
                System.out.println("Invalid choice");
            }
        }
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
            String query = "SELECT * FROM account WHERE password=?";

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
                        System.out.println();
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
        String accountInsert = "INSERT INTO account VALUES(?,?,?,?,?)";

        try {
            PreparedStatement stmt1 = conn.prepareStatement(accountInsert);
            stmt1.setString(1, username);
            stmt1.setString(2, email);
            stmt1.setString(3, dob);
            stmt1.setString(4, country);
            stmt1.setString(5, password1);
            stmt1.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void listRecipes(Connection conn) {
        try {
            String q = "SELECT * " + "FROM recipe r JOIN user_recipes ur USING(recipe_id) " + "WHERE username=?";
            PreparedStatement stmt = conn.prepareStatement(q);
            stmt.setString(1, currUser);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int recid = rs.getInt("recipe_id");
                String name = rs.getString("title");
                Timestamp date = rs.getTimestamp("date_posted");
                String strDate = parseDate(date);

                Blob ingredients = rs.getBlob("ingredients");
                byte[] blobIngrBytes = ingredients.getBytes(1, (int) ingredients.length());
                String strIngr = new String(blobIngrBytes);

                Blob instructions = rs.getBlob("instructions");
                byte[] blobInstrBytes = instructions.getBytes(1, (int) instructions.length());
                String strInstr = new String(blobInstrBytes);

                System.out.println("(" + recid + "): " + name);
                System.out.println("Date Posted: " + strDate);
                System.out.println("Ingredients: " + strIngr);
                System.out.println("Instructions:");
                printInstructions(strInstr);

                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error in viewing recipes");
        }
    }

    private static void printInstructions(String strInstr) {
        String[] instructionInfo = strInstr.split("[,]+\\s+");

        for (int i = 0; i < instructionInfo.length; i++) {
            System.out.println("    " + instructionInfo[i]);
        }
    }

    private static String parseDate(Timestamp date) {
        String[] dateInfo = date.toString().split("[ ]+");
        return dateInfo[0];
    }

    private static void listNewsFeed(Connection conn) {
        try {
            String q = "NEED TO GET NEWS FEED WORKING BOIIIII";
            PreparedStatement stmt = conn.prepareStatement(q);
            stmt.setString(1, currUser);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int recid = rs.getInt("recipe_id");
                String name = rs.getString("title");
                Timestamp date = rs.getTimestamp("date_posted");
                String strDate = parseDate(date);

                Blob ingredients = rs.getBlob("ingredients");
                byte[] blobIngrBytes = ingredients.getBytes(1, (int) ingredients.length());
                String strIngr = new String(blobIngrBytes);

                Blob instructions = rs.getBlob("instructions");
                byte[] blobInstrBytes = instructions.getBytes(1, (int) instructions.length());
                String strInstr = new String(blobInstrBytes);

                System.out.println("(" + recid + "): " + name);
                System.out.println("Date Posted: " + strDate);
                System.out.println("Ingredients: " + strIngr);
                System.out.println("Instructions:");
                printInstructions(strInstr);

                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error in viewing recipes");
        }
    }

    public static void createRecipeScreen(Connection conn) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter recipe title: ");
        String title = sc.nextLine();
        System.out.print("Enter number of ingredients: ");
        int numIngredients = sc.nextInt();
        StringBuilder ingredients = new StringBuilder();
        System.out.println("Enter " + numIngredients + " ingredients:");
        for (int i = 0; i <= numIngredients; i++) {
            ingredients.append(sc.nextLine());
        }
        System.out.print("Enter number of instructions: ");
        int numInstructions = sc.nextInt();
        StringBuilder instructions = new StringBuilder();
        System.out.println("Enter " + numInstructions + " instructions:");
        for (int i = 0; i <= numInstructions; i++) {
            instructions.append(sc.nextLine());
        }
        String recipeInsert = "INSERT INTO recipe (title, date_posted, view_flag, ingredients, instructions) VALUES (?, CURRENT_TIMESTAMP, TRUE, ?, ?)";
        try {
            PreparedStatement stmt = conn.prepareStatement(recipeInsert);
            stmt.setString(1, title);
            stmt.setString(2, ingredients.toString());
            stmt.setString(3, instructions.toString());
            stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error: failed to add recipe");
            e.printStackTrace();
        }
        // insert into food and 
        sc.close();
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