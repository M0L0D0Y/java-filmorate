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
SELECT film_liked_users.film_id, name, description, release_date, duration
FROM films RIGHT JOIN film_liked_users ON films.film_id = film_liked_users.film_id
GROUP BY film_liked_users.film_id
ORDER BY COUNT(user_id) DESC
LIMIT N;
```

Получение списка общих друзей с другим пользователем(id = 1 и id=2)

```
SELECT * FROM users WHERE user_id IN(
    SELECT * FROM (
        SELECT friend_id FROM (
            SELECT friend_id FROM friendship WHERE user_id = 1)
            WHERE friend_id IN (
                SELECT friend_id FROM friendship WHERE user_id = 2)));
```



