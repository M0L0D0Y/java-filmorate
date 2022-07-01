package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);

    private final FilmStorage memoryFilmStorage;
    private final UserStorage memoryUserStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmService(@Qualifier("InDataBaseFilm") FilmStorage memoryFilmStorage,
                       @Qualifier("InDataBaseUser") UserStorage memoryUserStorage, JdbcTemplate jdbcTemplate) {
        this.memoryFilmStorage = memoryFilmStorage;
        this.memoryUserStorage = memoryUserStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addLikeFilm(long filmId, long userId) throws NotFoundException {
        Film film = memoryFilmStorage.getFilm(filmId);//для проверки существования такого id
        User user = memoryUserStorage.getUser(userId); //для проверки существования такого id
        String query = "INSERT INTO 'film_liked_users' VALUES(?, ?)";
        jdbcTemplate.update(query, filmId, userId);
        log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, filmId);
    }

    public void deleteLike(long filmId, long userId) throws NotFoundException {
        Film film = memoryFilmStorage.getFilm(filmId);
        User user = memoryUserStorage.getUser(userId);
        String query = "DELETE  FROM 'film_liked_users' WHERE 'film_id' = ?";
        jdbcTemplate.update(query, filmId);
        log.info("Пользователь с id {} удалил лайк фильму с id {}", userId, filmId);
    }

    public List<Film> getMostPopularFilms(long count) {
       /* List<Film> filmList = new LinkedList<>(memoryFilmStorage.getAllFilm());
        filmList.sort(Comparator.comparingInt(o -> o.getLikedUsers().size() * (-1)));//reversed() почему-то не работает
        return filmList.stream()
                .limit(count)
                .collect(Collectors.toList());*/

        String query = "SELECT *\n" +
                "FROM 'films'\n" +
                "WHERE film_id IN (\n" +
                "    SELECT film_id,\n" +
                "    COUNT (user_id)\n" +
                "    FROM 'film_liked'\n" +
                "    GROUP BY film_id\n" +
                "    ORDER BY COUNT (user_id) DESC)\n";
        List<Film> filmList = new LinkedList<>(jdbcTemplate.query(
                query,
                new Object[]{count},
                new BeanPropertyRowMapper<>(Film.class)));
        return filmList.stream()
                .limit(count)
                .collect(Collectors.toList());
    }
}
