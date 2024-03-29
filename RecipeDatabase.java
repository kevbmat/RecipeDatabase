
/**
 * This program is a recipe social network which leverages the cpsc321_groupC_DB on the cps-database at Gonzaga University
 * @author: Cole deSilva, Andrew Flagstead, Kevin Mattappally, Brandon Clark
 * @version: v1.0
 * @assignment: Final Project!
 * @date: 12/5/2019
 * @course: CPSC321
 * 
 * Possible improvemnts to be made: 
 *      (1) make input scanner and connection variables global variables and initialize them in main 
 *          so that we dont have to pass them everywhere
 *      (2) make all of our SQL strings consistent in terms of style and ensure all are using prepared statements
 *      (3) work on better input validation
 */
import java.util.*;
import java.io.*;
import java.nio.charset.MalformedInputException;
import java.sql.*;
import java.sql.Date;
import java.util.Scanner;
import com.mysql.jdbc.Driver;

public class RecipeDatabase {
    private static String currUser = "";

    public static void main(String[] args) {

        Scanner userinput = new Scanner(System.in);
        // menu which promts database user to login to database
        Connection dbConn = connectToDB();

        // once into the DB, intro menu for our program
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

        // once authenticated, the main menu will be presented
        mainMenu(userinput, dbConn);
    }

    /**
     * the intro menu for our program where users can login with an existing account
     * or create a new account
     * 
     * @param sc
     * @param conn
     * @return an integer value if the user successfully logged in, created an
     *         account, or exited the program.
     */
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

    /**
     * login input validation loop makes sure that the username and password are
     * valid
     */
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

    /**
     * main menu for our program where the user can go into the different menus:
     * recipe menu, social menu, or exit the program
     * 
     * @param sc
     * @param conn
     */
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
                socialMenu(sc, conn);
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

    /**
     * recipe menu for the program where users can be taken to functions to
     * view/add/update/delete recipes
     * 
     * @param sc
     * @param conn
     */
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
                System.out.println();
                break;
            case '2':
                createRecipeScreen(sc, conn);
                System.out.println();
                break;
            case '3':
                updateRecipe(sc, conn);
                System.out.println();
                break;
            case '4':
                deleteRecipe(sc, conn);
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

    /**
     * social menu for our application where users can look at their news feed, see
     * trending users, follow/unfollow people, and add comments to recipes.
     * 
     * @param sc
     * @param conn
     */
    private static void socialMenu(Scanner sc, Connection conn) {
        boolean inSocialMenu = true;

        while (inSocialMenu) {
            System.out.println("Select option below");
            System.out.println("(1) News Feed");
            System.out.println("(2) View Trending Users");
            System.out.println("(3) Follow Someone");
            System.out.println("(4) Unfollow Someone");
            System.out.println("(5) Add comment");
            System.out.println("(6) Return to Previous Menu");

            char input = sc.nextLine().charAt(0);
            switch (input) {
            case '1':
                System.out.println();
                listNewsFeed(conn);
                break;
            case '2':
                System.out.println();
                listTrending(sc, conn);
                break;
            case '3':
                System.out.println();
                followMenu(sc, conn);
                break;
            case '4':
                System.out.println();
                unfollowMenu(sc, conn);
                break;
            case '5':
                System.out.println();
                addComment(sc, conn);
            case '6':
                inSocialMenu = false;
                System.out.println();
                break;
            default:
                System.out.println("Invalid choice");
            }
        }
    }

    /**
     * function to add a comment to a specific recipe. first lists recipes you can
     * comment on, then prompts user to add a comment.
     * 
     * @param sc
     * @param conn
     */
    public static void addComment(Scanner sc, Connection conn) {
        try {
            String query = "SELECT r.recipe_id, r.title, ur.username AS post_user FROM recipe r NATURAL JOIN user_recipes ur JOIN following f ON (ur.username = f.account2) WHERE (f.account1 = ?) ORDER BY r.recipe_id DESC";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, currUser);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int recipe_id = rs.getInt("recipe_id");
                String title = rs.getString("title");
                String post_user = rs.getString("post_user");
                System.out.println("(" + recipe_id + ")" + title + " by " + post_user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error with adding comments");
        }
        System.out.print("Enter an id to comment on: ");
        int postIdToCommentOn = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter your comment: ");
        String strComment = sc.nextLine();
        String commentAddition = "INSERT INTO comments (username, recipe_id, comment) VALUES (?, ?, ?)";
        try {
            byte[] byteContent = strComment.getBytes();
            Blob blob = conn.createBlob();
            blob.setBytes(1, byteContent);
            PreparedStatement stmtComment = conn.prepareStatement(commentAddition);
            stmtComment.setString(1, currUser);
            stmtComment.setInt(2, postIdToCommentOn);
            stmtComment.setString(3, strComment);
            stmtComment.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error: failed to add comment");
            e.printStackTrace();
        }
    }

    /**
     * function to list the trending user(s) a trending user is the person or people
     * with the most ammount of posted recipes.
     * 
     * @param sc
     * @param conn
     */
    public static void listTrending(Scanner sc, Connection conn) {
        System.out.println("Trending User(s):");
        String trending = "select username, COUNT(*) as number_of_recipes " + "from user_recipes "
                + "GROUP BY username " + "HAVING COUNT(*) = (select max(most_recipes) from "
                + "(select username, count(*) as most_recipes " + "from user_recipes group by username) as sq)";

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(trending);
            while (rs.next())
                System.out.println(rs.getString(1) + ": " + rs.getString(2));
        }

        catch (SQLException e) {
            System.out.println(e);
        }

        System.out.println();
        System.out.println();

    }

    /**
     * checks to see if a specific user actually exists in the DB currently
     * 
     * @param conn
     * @param username
     * @return
     */
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

    /**
     * functionality for following another user first prints the users that the
     * current user is not following the prompts them to follow someone
     * 
     * @param sc
     * @param conn
     */
    private static void followMenu(Scanner sc, Connection conn) {
        boolean isNotValidInput = true;
        // finding everyone that the person does not follow
        String query1 = "SELECT DISTINCT account2 " + "FROM following " + "WHERE account2 NOT IN (SELECT account2 "
                + "FROM following " + "WHERE account1=? OR account2=?)";

        try {
            PreparedStatement stmt = conn.prepareStatement(query1);
            stmt.setString(1, currUser);
            stmt.setString(2, currUser);

            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("You already follow everyone!");
                isNotValidInput = false;
            } else {
                System.out.println("You are currently not following:");
                while (rs.next()) {
                    String name = rs.getString("account2");
                    System.out.println(name);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println();

        while (isNotValidInput) {
            System.out.print("Please enter the name of the person you want to follow: ");
            String answer = sc.nextLine();

            if (answer.equals(currUser)) {
                System.out.println("Error: You cannot follow yourself!");
                System.out.println();
            } else if (isAlreadyFollowing(conn, answer)) {
                System.out.println("Error: You cannot follow someone youre already following!");
                System.out.println();
            } else if (answer != "" && doesUserExist(conn, answer)) {
                String query2 = "INSERT INTO following VALUES(?,?)";
                try {
                    PreparedStatement stmt2 = conn.prepareStatement(query2);
                    stmt2.setString(1, currUser);
                    stmt2.setString(2, answer);
                    stmt2.execute();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println("Follow successful!");
                System.out.println();
                isNotValidInput = false;
                break;
            } else {
                System.out.println("Error: Invalid User!");
                System.out.println();
            }
        }
    }

    // function to determine if a user is already following another user
    private static boolean isAlreadyFollowing(Connection conn, String name) {
        // sets up query to determine if the current user who is signed in is following
        // another account and what account it is.
        String query = "SELECT * FROM following WHERE account1=? AND account2=?";
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, currUser);
            stmt.setString(2, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * function menu to unfollow a user. Will run if user chooses to unfollow
     * someone.
     * 
     * @param sc
     * @param conn
     */
    private static void unfollowMenu(Scanner sc, Connection conn) {
        boolean isNotValidInput = true;
        // sets up query to determine who you are following
        String query = "SELECT account2 FROM following WHERE account1=?";
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, currUser);
            ResultSet rs = stmt.executeQuery();
            // check to ensure they are following anyone. If they are following people, that
            // list will be outputted.
            if (!rs.isBeforeFirst()) {
                System.out.println("You are currently not following anyone!");
                isNotValidInput = false;
            } else {
                System.out.println("You are currently following:");
                while (rs.next()) {
                    String name = rs.getString("account2");
                    System.out.println(name);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println();

        while (isNotValidInput) {
            System.out.print("Please enter the name of the person you want to unfollow: ");
            String answer = sc.nextLine();
            // catch cases to ensure it happens only if possible
            if (answer.equals(currUser)) {
                System.out.println("Error: You cannot unfollow yourself!");
                System.out.println();
            } else if (answer != "" && doesUserExist(conn, answer) && isAlreadyFollowing(conn, answer)) {
                // setting up query to delete from database
                String query2 = "DELETE FROM following WHERE account1=? AND account2=?";
                try {
                    PreparedStatement stmt2 = conn.prepareStatement(query2);
                    stmt2.setString(1, currUser);
                    stmt2.setString(2, answer);
                    stmt2.execute();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println("Unfollow successful!");
                System.out.println();
                isNotValidInput = false;
                break;
            } else {
                System.out.println("Error: Invalid User!");
                System.out.println();
            }
        }
    }

    /**
     * function to delete recipes based on user input
     * 
     * @param userinput
     * @param conn
     */
    private static void deleteRecipe(Scanner userinput, Connection conn) {
        // bool to validate if it is or is not a valid input.
        boolean isNotValidInput = true;
        int recipeId = 0;
        // validation of it is a valid recipe to be deleted
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
        // queries to delete recipe from user_recipes and recipe table
        // String query1 = "DELETE FROM recipe_type WHERE recipe_id=?";
        String query2 = "DELETE FROM user_recipes WHERE recipe_id=?";
        String query3 = "DELETE FROM recipe WHERE recipe_id=?";

        try {
            // PreparedStatement stmt1 = conn.prepareStatement(query1);
            // stmt1.setInt(1, recipeId);
            // stmt1.execute();
            // executing the queries to delete recipes from the two tables
            PreparedStatement stmt2 = conn.prepareStatement(query2);
            stmt2.setInt(1, recipeId);
            stmt2.execute();
            // setting the values and executing the query to delte the recipes
            PreparedStatement stmt3 = conn.prepareStatement(query3);
            stmt3.setInt(1, recipeId);
            stmt3.execute();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error in deleting recipe");
        }

    }

    /**
     * validation of if the recipe id is valid or not
     * 
     * @param id
     * @param conn
     * @return
     */
    private static boolean isValidRecipeId(int id, Connection conn) {
        if (id <= 0) {
            return false;
        }

        try {
            // query to find recipes with a given recipe id
            String query = "SELECT * FROM recipe WHERE recipe_id=?";
            // setting
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, id);
            // creating result set
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }

    /**
     * validation that twopasswords are equal when creating account
     * 
     * @param conn
     * @param password
     * @return
     */
    private static boolean isValidPassword(Connection conn, String password) {
        if (password.equals("")) {
            return false;
        }
        try {
            // setting up query to find password from account
            String query = "SELECT * FROM account WHERE password=?";
            // setting up prepared statement for query
            PreparedStatement stmt = conn.prepareStatement(query);
            // setting values for password
            stmt.setString(1, password);
            // creating result set and executing query
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * create account screen
     * 
     * @param sc
     * @param conn
     */
    public static void createAccountScreen(Scanner sc, Connection conn) {
        // declartation of values to be entered
        boolean isNotValidInput = true;
        String username = "";
        String password1 = "";
        String email = "";
        String country = "";
        String dob = "";
        // validation that input works
        while (isNotValidInput) {
            Console console = System.console();
            // outputting request for values ot be inputted and having user enter them
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
                    // validating passwords entered are equal to one another
                    if ((username.equals("") || password1.equals("") || email.equals("") || country.equals("")
                            || dob.equals(""))
                            || (username.length() > 30 || password1.length() > 30 || country.length() > 2)) {
                        System.out.println("Invalid input. Please enter valid input.");
                    }
                    // excutes granted all values are valid
                    else {
                        isNotValidInput = false;
                        System.out.println();
                        break;
                    }
                    // output if passwords do not match
                } else {
                    System.out.println("Your passwords do not match! Try again!");
                }
                // outputs if user trys to create an account when one already exists
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

    /**
     * lists out each of the recipes that the user has made doesn't include recipes
     * that the user is following
     * 
     * @param conn
     */
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

    /**
     * prints out each of the recipe instructions in a nicely formatted fashion
     * 
     * @param strInstr
     */
    private static void printInstructions(String strInstr) {
        String[] instructionInfo = strInstr.split("[,]+\\s*");

        for (int i = 0; i < instructionInfo.length; i++) {
            System.out.println("    " + instructionInfo[i]);
        }
    }

    /**
     * parses the date from SQL into a String format
     * 
     * @param date
     * @return a String that represents the given date
     */
    private static String parseDate(Timestamp date) {
        String[] dateInfo = date.toString().split("[ ]+");
        return dateInfo[0];
    }

    /**
     * prints out the news feed in the application shows recipes and recipe comments
     * for each user that the current user is following
     * 
     * @param conn
     */
    private static void listNewsFeed(Connection conn) {
        try {
            String query = "SELECT r.recipe_id, r.title, r.ingredients, r.instructions, ur.username AS post_user FROM recipe r NATURAL JOIN user_recipes ur JOIN following f ON (ur.username = f.account2) WHERE (f.account1 = ?) ORDER BY r.recipe_id DESC";
            String commentQuery = "SELECT c.username, c.comment FROM comments c WHERE recipe_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, currUser);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int recipe_id = rs.getInt("recipe_id");
                String title = rs.getString("title");
                Blob ingredients = rs.getBlob("ingredients");
                Blob instructions = rs.getBlob("instructions");
                String post_user = rs.getString("post_user");
                PreparedStatement commentStmt = conn.prepareStatement(commentQuery);
                commentStmt.setInt(1, recipe_id);
                ResultSet commentRs = commentStmt.executeQuery();
                StringBuilder comments = new StringBuilder();
                while (commentRs.next()) {
                    String commentUsername = commentRs.getString("username");
                    Blob commentBody = commentRs.getBlob("comment");
                    byte[] blobCommmentBodyBytes = commentBody.getBytes(1, (int) commentBody.length());
                    String stringCommentBody = new String(blobCommmentBodyBytes);
                    comments.append(commentUsername + ": " + stringCommentBody + "\n");
                }
                // Timestamp date = rs.getTimestamp("date_posted");
                // String strDate = parseDate(date);
                byte[] blobIngrBytes = ingredients.getBytes(1, (int) ingredients.length());
                String strIngr = new String(blobIngrBytes);
                byte[] blobInstrBytes = instructions.getBytes(1, (int) instructions.length());
                String strInstr = new String(blobInstrBytes);
                System.out.println(post_user + ": " + title);
                System.out.println("Ingredients: " + strIngr);
                System.out.println("Instructions: ");
                printInstructions(strInstr);
                System.out.println("---Comment section---");
                System.out.println(comments.toString());
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error in viewing recipes");
        }
    }

    /**
     * allows the user to enter their own recipe into the application
     * 
     * @param sc
     * @param conn
     */
    public static void createRecipeScreen(Scanner sc, Connection conn) {
        System.out.print("Enter recipe title: ");
        String title = sc.nextLine();
        System.out.print("Enter number of ingredients: ");
        int numIngredients = sc.nextInt();
        sc.nextLine();
        StringBuilder ingredients = new StringBuilder();
        System.out.println("Enter " + numIngredients + " ingredients one at a time:");
        for (int i = 0; i < numIngredients; i++) {
            if (i == numIngredients - 1) {
                ingredients.append(sc.nextLine());
            } else {
                ingredients.append(sc.nextLine() + ",");
            }
        }
        System.out.print("Enter number of instructions: ");
        int numInstructions = sc.nextInt();
        sc.nextLine();
        StringBuilder instructions = new StringBuilder();
        System.out.println("Enter " + numInstructions + " instructions one at a time");
        for (int i = 0; i < numInstructions; i++) {
            instructions.append(sc.nextLine() + ", ");
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
        String lastInsertIdQuery = "SELECT LAST_INSERT_ID()";
        int lastInsertId = -1;
        try {
            PreparedStatement stmt = conn.prepareStatement(lastInsertIdQuery);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            lastInsertId = rs.getInt("LAST_INSERT_ID()");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: last id failed");
        }
        // insert into user_recipe
        String userRecipeInsert = "INSERT INTO user_recipes VALUES (?, ?)";
        try {
            PreparedStatement stmt = conn.prepareStatement(userRecipeInsert);
            stmt.setString(1, currUser);
            stmt.setInt(2, lastInsertId);
            stmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error: failed to add user_recipe");
            e.printStackTrace();
        }
    }

    /**
     * allows users to update any of their current recipes in the application
     * 
     * @param sc
     * @param con
     */
    public static void updateRecipe(Scanner sc, Connection con) {
        System.out.println("Updating Recipe \n");

        try {
            Statement stmt = con.createStatement();
            ResultSet set = stmt
                    .executeQuery("SELECT u.recipe_id, r.title FROM user_recipes u JOIN recipe r USING(recipe_id)"
                            + " WHERE u.username = '" + currUser + "'");// replace Alice with currUser

            System.out.println("Your Recipes: ");
            while (set.next()) {
                System.out.println(set.getString(1) + ": " + set.getString(2));
            }
        } catch (SQLException e) {
            System.out.println(e);
            return;
        }

        System.out.print("\nWhich recipe would you like to update or change? Enter id number: ");
        String choice = sc.nextLine();
        int id = Integer.parseInt(choice);
        if (isUpdatableRecipe(id, con)) {
            System.out.print("\nPlease enter a new title: ");
            String newTitle = sc.nextLine();
            System.out.print("\nPlease enter new ingredients (separated by commas): ");
            String newIngredients = sc.nextLine();
            System.out.print("\nPlease enter new instructions (separated by commas): ");
            String newInstructions = sc.nextLine();
            String update = ("UPDATE recipe SET title='" + newTitle + "', ingredients = '" + newIngredients
                    + "', instructions = '" + newInstructions + "' WHERE recipe_id = " + choice);
            try {
                PreparedStatement stmt1 = con.prepareStatement(update);
                stmt1.execute(update);
            } catch (SQLException e) {
                System.out.println(e);
            }
        } else {
            System.out.println("Please enter a valid recipe id that you would like to update");
        }
        // add input validation here, checking that choice is in the first column of
        // ResultSet

    }

    /**
     * checks whether the given recipe id is a valid updatable recipe
     * 
     * @param id
     * @param conn
     * @return either true or false depending on if the id is a valid recipe id
     */
    private static boolean isUpdatableRecipe(int id, Connection conn) {
        String query = "SELECT u.recipe_id " + "FROM user_recipes u JOIN recipe r USING(recipe_id) "
                + "WHERE u.username=? AND u.recipe_id=?";

        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, currUser);
            stmt.setInt(2, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * method that makes the initial connection to the database
     * 
     * @return a Connection that can be used within the rest of the application to
     *         process queries and updates
     */
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