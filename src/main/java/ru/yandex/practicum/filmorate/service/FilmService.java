package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.service.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.service.mappers.RatingMapper;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage memoryFilmStorage;
    private final UserStorage memoryUserStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmService(@Qualifier("DatabaseFilmStorage") FilmStorage memoryFilmStorage,
                       @Qualifier("DatabaseUserStorage") UserStorage memoryUserStorage,
                       JdbcTemplate jdbcTemplate) {
        this.memoryFilmStorage = memoryFilmStorage;
        this.memoryUserStorage = memoryUserStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addLikeFilm(long filmId, long userId) throws NotFoundException {
        checkExistId(filmId, userId);
        String query = "INSERT INTO FILM_LIKED_USERS (FILM_ID, USER_ID) VALUES(?,?) ";
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
        String query = "SELECT FILM_LIKED_USERS.FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION " +
                "FROM FILMS RIGHT JOIN FILM_LIKED_USERS ON FILMS.FILM_ID = FILM_LIKED_USERS.FILM_ID " +
                "GROUP BY FILM_LIKED_USERS.FILM_ID " +
                "ORDER BY COUNT(USER_ID) DESC";
        List<Film> films = jdbcTemplate.query(
                        query,
                        new FilmMapper())
                .stream()
                .limit(count)
                .collect(Collectors.toList());
        List<Film> updateFilms = new LinkedList<>();
        for (Film film : films) {
            film = memoryFilmStorage.getFilm(film.getId());
            updateFilms.add(film);
        }
        return updateFilms;
    }

    public Collection<Genre> getAllGenres() {
        String query = "SELECT * FROM GENRES";
        log.info("Все жанры получены");
        return jdbcTemplate.query(
                query,
                new GenreMapper());
    }

    public Genre getGenreById(int genreID) {
        String query = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
        Genre genre = jdbcTemplate.query(query,
                        new GenreMapper(),
                        genreID)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Жанр с идентификатором " + genreID + " не найден."));
        log.info("Жанр с id = {} получен", genreID);
        return genre;
    }

    public Collection<Rating> getAllRating() {
        String query = "SELECT * FROM RATING";
        log.info("Все рейтинги получены");
        return jdbcTemplate.query(
                query,
                new RatingMapper());
    }

    public Rating getRatingById(int ratingId) {
        log.info("Ищем рейтинрг с id = {} получен", ratingId);
        System.out.println(ratingId);
        String query = "SELECT * FROM RATING WHERE RATING_ID = ?";
        Rating rating = jdbcTemplate.query(query,
                        new RatingMapper(),
                        ratingId)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Жанр с идентификатором " + ratingId + " не найден."));
        log.info("Рейтинг с id = {} получен", ratingId);
        return rating;
    }

    private void checkExistId(long filmId, long userId) throws NotFoundException {//для проверки существования таких id
        Film film = memoryFilmStorage.getFilm(filmId);
        User user = memoryUserStorage.getUser(userId);
    }

}
