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
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.service.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.service.mappers.RatingMapper;
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
        //checkExistId(filmId, userId);
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
        for (Film film: films){
            film.setMpa(getDateRatingById(film.getId()));
            film.setGenres(getDateGenreById(film.getId()));
        }
        return films;
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

    private void checkExistId(long filmId, long userId) throws NotFoundException {
        Film film = memoryFilmStorage.getFilm(filmId);//для проверки существования такого id
        User user = memoryUserStorage.getUser(userId); //для проверки существования такого id
    }

    private Rating getDateRatingById(long id) {
        String queryGetDateRatingById = "SELECT * FROM RATING WHERE RATING_ID IN (" +
                "SELECT RATING_ID FROM FILM_RATING WHERE FILM_ID = ?)";
        Rating rating = jdbcTemplate.query(
                        queryGetDateRatingById,
                        new RatingMapper(),
                        id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Рейтинг для фильма с идентификатором " + id + " не найден."));
        log.info("Значения из таблицы FILM_RATING получены по id {}", id);
        return rating;
    }

    private List<Genre> getDateGenreById(long id) {
        String queryGetDateGenreById = "SELECT * FROM GENRES WHERE GENRE_ID IN (" +
                "SELECT GENRE_ID FROM FILM_GENRE WHERE FILM_ID = ?)";
        List<Genre> genres = jdbcTemplate.query(
                queryGetDateGenreById,
                new GenreMapper(),
                id);
        log.info("Значения из таблицы FILM_GENRE получены по id {}", id);
        return genres;
    }
    //TODO ПОДУМАТЬ НАД ДУБЛИРОВАНИЕМ МЕТОДОВ getDateRatingById И getDateGenreById


}
