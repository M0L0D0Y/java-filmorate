# java-filmorate
Template repository for Filmorate project.
![](schema.png)
Примеры SQL запросов:

Получение всех фильмов
```
SELECT *
FROM films;
```
Получение всех пользователей
```
SELECT *
FROM users;
```
Получение топ N наиболее популярных фильмов
```
SELECT *
FROM films
WHERE film_id IN (
    SELECT film_id,
    COUNT (user_id)
    FROM film_liked
    GROUP BY film_id
    ORDER BY COUNT (user_id) DESC)
LIMIT N;
```
Получение списка общих друзей с другим пользователем(id = 1 и id=2)
```
SELECT *
FROM users
WHERE user_id IN(
    SELECT *
    FROM (
        SELECT friend_id
        FROM (
            SELECT friend_id
            FROM friendship
            WHERE user_id = 1
            AND status_id IN(
                SELECT status_id
                FROM status_friendship
                WHERE name = "подтверждена"))
        WHERE friend_id IN (
            SELECT friend_id
            FROM friendship
            WHERE user_id = 2
            AND status_id IN(
                SELECT status_id
                FROM status_friendship
                WHERE name = "подтверждена"))));
```



