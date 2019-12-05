SELECT u.recipe_id, r.title
FROM user_recipes u JOIN recipe r USING(recipe_id)
WHERE u.username='Alice';