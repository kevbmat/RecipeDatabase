USE cpsc321_groupC_DB;

DROP TABLE IF EXISTS recipe_type;
DROP TABLE IF EXISTS food;
DROP TABLE IF EXISTS user_recipes;
DROP TABLE IF EXISTS following;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS recipe;

CREATE TABLE account (
    username VARCHAR(30) NOT NULL,
    email VARCHAR(30) NOT NULL,
    dob VARCHAR(180) NOT NULL,
    country_residence VARCHAR(2),
    password VARCHAR(30) NOT NULL,
    PRIMARY KEY (username)
);

CREATE TABLE following (
    account1 VARCHAR(30) NOT NULL,
    account2 VARCHAR(30) NOT NULL,
    PRIMARY KEY (account1, account2),
    FOREIGN KEY (account1) REFERENCES account (username),
    FOREIGN KEY (account2) REFERENCES account (username)
);

CREATE TABLE recipe (
    recipe_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    title VARCHAR(30) NOT NULL,
    date_posted TIMESTAMP NOT NULL,
    view_flag BOOLEAN NOT NULL,
    ingredients BLOB NOT NULL,
    instructions BLOB NOT NULL,
    PRIMARY KEY (recipe_id)
);
CREATE TABLE comments(
    comment_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    username VARCHAR(30) NOT NULL,
    recipe_id INT UNSIGNED NOT NULL,
    comment BLOB NOT NULL,
    PRIMARY KEY(comment_id),
    FOREIGN KEY(username) REFERENCES account(username),
    FOREIGN KEY(recipe_id) REFERENCES recipe(recipe_id) ON UPDATE CASCADE
);

CREATE TABLE user_recipes (
    username VARCHAR(30) NOT NULL,
    recipe_id INT UNSIGNED NOT NULL,
    PRIMARY KEY (username, recipe_id),
    FOREIGN KEY (username) REFERENCES account (username),
    FOREIGN KEY (recipe_id) REFERENCES recipe (recipe_id) ON UPDATE CASCADE
);

CREATE TABLE food (
    food_type VARCHAR(30) NOT NULL,
    PRIMARY KEY (food_type)
);

CREATe TABLE recipe_type (
    recipe_id INT UNSIGNED NOT NULL,
    food_type VARCHAR(30) NOT NULL,
    PRIMARY KEY (recipe_id, food_type),
    FOREIGN KEY (food_type) REFERENCES food (food_type),
    FOREIGN KEY (recipe_id) REFERENCES recipe (recipe_id) ON UPDATE CASCADE
);

INSERT INTO food VALUES
    ("Chinese"),
    ("Indian"),
    ("Japanese"),
    ("Italian"),
    ("Mexican"),
    ("American");

INSERT INTO account VALUES
    ("Alice", "alice@outlook.com", "March 2, 1992", "US", "passalice"),
    ("Bob", "bob@outlook.com", "March 2, 1993", "CA", "passbob"),
    ("Charlie", "charlie@outlook.com", "July 3, 1994", "MX", "passcharlie"),
    ("Delta", "delta@outlook.com", "November 13, 1993", "IN", "passdelta");



INSERT INTO following VALUES
    ("Alice", "Bob"),
    ("Alice", "Delta"),
    ("Alice", "Charlie"),
    ("Charlie", "Bob"),
    ("Delta", "Bob"),
    ("Bob", "Alice"),
    ("Bob", "Delta"),
    ("Delta", "Charlie");

INSERT INTO recipe VALUES
    (1, "coleslaw", CURRENT_TIMESTAMP, FALSE, "cabbage, carrots, onions", "take everything and mix it"),
    (2, "pasta", CURRENT_TIMESTAMP, TRUE, "pasta, sauce, cheese", "put it in the pot, and cook"),
    (3, "fried rice", CURRENT_TIMESTAMP, TRUE, "rice, peas, carrots", "fry the rice and mix all in"),
    (4, "idli", CURRENT_TIMESTAMP, TRUE, "idli, chutney, sambar", "make batter, then cook"),
    (5, "orange chicken", CURRENT_TIMESTAMP, TRUE, "chicken, curry, pepper", "get the chicken then add the pepper"),
    (6, "chow mein", CURRENT_TIMESTAMP, FALSE, "noodles, celery, pepper", "get the noodles and add everything else"),
    (7, "dosa", CURRENT_TIMESTAMP, TRUE, "dosa, sambar, chutney", "make the dosa, add the sambar"),
    (8, "sushi", CURRENT_TIMESTAMP, FALSE, "rice, seaweed, fish", "put it together, eat it raw"),
    (9, "burrito", CURRENT_TIMESTAMP, TRUE, "rice, beans, chicken, lettuce, chile verde, cheese", "cook all ingredients, put equal portions in, wrap burrito"),
    (10, "pizza", CURRENT_TIMESTAMP, FALSE, "pizza dough, tomato sauce, pepperoni, sausage", "roll out dough, spread tomato sauce evenly on dough, sprinkle cheese, place pepperoni and sausage evenly."),
    (11, "cheese burger", CURRENT_TIMESTAMP, FALSE, "burger, cheese, bun, ketchup", "grill burger to desired rareness, place cheese on burger until melted, take burger off grill and place on bun, squeeze ketchup on burger."),
    (12, "fried chicken", CURRENT_TIMESTAMP, TRUE, "batter, chicken, oil", "heat oil, place chicken in batter, fry until crispy brown");

INSERT INTO comments VALUES
    (1, "Alice", 5, "Needs more spice!"),
    (2, "Delta", 3, "Where is the egg?"),
    (3, "Charlie", 4, "Do you fry?"),
    (4, 'Charlie', 1, 'My grandparents hated it'),
    (5, 'Alice', 1, 'block party success because of this'),
    (6, 'Delta', 1, 'my friends love me now'),
    (7, 'Bob', 1, 'just like panda'),
    (8, 'Charlie', 8, 'so authentic, i love it'),
    (9, 'Bob', 1, 'Coleslaw Dude!'),
    (10, 'Delta', 1, 'I love this'),
    (11, 'Charlie', 2, 'Very italian'),
    (12, 'Alice', 3, 'OMG so delicious XD');

INSERT INTO recipe_type VALUES
    (1, "American"),
    (2, "Italian"),
    (3, "Chinese"),
    (4, "Indian"),
    (5, "Chinese"),
    (6, "Chinese"),
    (7, "Indian"),
    (8, "Japanese"),
    (9, "Mexican");

INSERT INTO user_recipes VALUES
    ("Alice", 1),
    ("Alice", 2),
    ("Bob", 3),
    ("Bob", 4),
    ("Charlie", 5),
    ("Charlie", 6),
    ("Delta", 7),
    ("Delta", 8);

/*example queries*/
SELECT * FROM food;

/*finds CA users*/
SELECT username 
    FROM account
    WHERE country_residence = "CA";

/*get charlie's recipes*/
SELECT recipe_id FROM user_recipes
    WHERE username = 'Charlie';

/*see who's following bob*/
SELECT account2 
    FROM following
    WHERE account1 = 'Bob';

/*get recipe titles with their category*/
SELECT r.title, t.food_type
    FROM recipe r JOIN recipe_type t USING(recipe_id);

/*finds how many recipes there are of each type*/
SELECT food_type, COUNT(*)
    FROM recipe_type
    GROUP BY food_type
    ORDER BY COUNT(*) DESC;

SELECT * 
FROM comments;