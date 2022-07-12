package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.mappers.FilmMapper;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component("DatabasePopularFilmsStorage")
public class DatabasePopularFilmsStorage implements PopularFilmsStorage {
    private final JdbcTemplate jdbcTemplate;

    public DatabasePopularFilmsStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLikeFilm(long filmId, long userId) {
        String query = "INSERT INTO FILM_LIKED_USERS (FILM_ID, USER_ID) VALUES(?,?) ";
        jdbcTemplate.update(query, filmId, userId);
        log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, filmId);
    }

    @Override
    public void deleteLike(long filmId, long userId) throws NotFoundException {
        String query = "DELETE  FROM FILM_LIKED_USERS WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(query, filmId, userId);
        log.info("Пользователь с id {} удалил лайк фильму с id {}", userId, filmId);
    }

    @Override
    public List<Film> getMostPopularFilms(long count) {
        String query = "SELECT FILM_LIKED_USERS.FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION " +
                "FROM FILMS RIGHT JOIN FILM_LIKED_USERS ON FILMS.FILM_ID = FILM_LIKED_USERS.FILM_ID " +
                "GROUP BY FILM_LIKED_USERS.FILM_ID " +
                "ORDER BY COUNT(USER_ID) DESC";
        return jdbcTemplate.query(
                        query,
                        new FilmMapper())
                .stream()
                .limit(count)
                .collect(Collectors.toList());

    }
}
