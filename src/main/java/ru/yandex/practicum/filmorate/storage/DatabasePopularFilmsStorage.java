package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Grades;
import ru.yandex.practicum.filmorate.service.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.service.mappers.GradesMapper;

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
    public void addLikeFilm(long filmId, long userId, int grade) {
        String query = "INSERT INTO FILM_GRADE_USERS (FILM_ID, USER_ID, GRADES) VALUES(?,?,?) ";
        jdbcTemplate.update(query, filmId, userId, grade);
        log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, filmId);
    }

    @Override
    public void deleteLike(long filmId, long userId) throws NotFoundException {
        String query = "DELETE FROM FILM_LIKED_USERS WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(query, filmId, userId);
        log.info("Пользователь с id {} удалил лайк фильму с id {}", userId, filmId);
    }

    @Override
    public List<Film> getMostPopularFilms(long count) {
        String query1 = "SELECT FILM_GRADE_USERS.FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION " +
                "FROM FILMS RIGHT JOIN FILM_GRADE_USERS ON FILMS.FILM_ID = FILM_GRADE_USERS.FILM_ID " +
                "GROUP BY FILM_GRADE_USERS.FILM_ID " +
                "ORDER BY COUNT(USER_ID) DESC";

        String query = "SELECT FILM_GRADE_USERS.FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, AVG(GRADES) " +
                "FROM FILMS RIGHT JOIN FILM_GRADE_USERS ON FILMS.FILM_ID = FILM_GRADE_USERS.FILM_ID " +
                "GROUP BY FILM_GRADE_USERS.FILM_ID " +
                "ORDER BY AVG(GRADES) DESC";
        return jdbcTemplate.query(
                        query,
                        new FilmMapper())
                .stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    public Grades getGrades(long filmId) {
        String query = "SELECT AVG(GRADES) FROM FILM_GRADE_USERS WHERE FILM_ID=? AND USER_ID IN (" +
                "SELECT DISTINCT USER_ID FROM FILM_GRADE_USERS)";
        return jdbcTemplate.query(query,
                        new GradesMapper(),
                        filmId)
                .stream()
                .findAny()
                .orElse(null);

    }
}
