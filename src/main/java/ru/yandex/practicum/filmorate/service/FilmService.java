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
import ru.yandex.practicum.filmorate.service.mappers.FilmMapper;
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
        checkExistId(filmId, userId);
        String query = "INSERT INTO FILM_LIKED_USERS VALUES(?, ?)";
        jdbcTemplate.update(query, filmId, userId);
        log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, filmId);
    }

    public void deleteLike(long filmId, long userId) throws NotFoundException {
        checkExistId(filmId, userId);
        String query = "DELETE  FROM FILM_LIKED_USERS WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(query, filmId, userId);
        log.info("Пользователь с id {} удалил лайк фильму с id {}", userId, filmId);
    }

    public List<Film> getMostPopularFilms(long count) {
        String query = "SELECT * FROM FILMS WHERE FILM_ID IN (" +
                "SELECT FILM_ID " +
                "FROM FILM_LIKED_USERS GROUP BY FILM_ID ORDER BY COUNT (USER_ID) DESC)";
        List<Film> films = jdbcTemplate.query(query, new FilmMapper());
        return films.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    private void checkExistId(long filmId, long userId) throws NotFoundException {
        Film film = memoryFilmStorage.getFilm(filmId);//для проверки существования такого id
        User user = memoryUserStorage.getUser(userId); //для проверки существования такого id
    }
}
