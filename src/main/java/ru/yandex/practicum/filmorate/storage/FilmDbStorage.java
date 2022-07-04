package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.Validator;
import ru.yandex.practicum.filmorate.service.FilmIdGenerator;
import ru.yandex.practicum.filmorate.service.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.service.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.service.mappers.RatingMapper;
import ru.yandex.practicum.filmorate.service.mappers.UserMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component("InDataBaseFilm")
public class FilmDbStorage implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);

    private final Validator validator;
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(Validator validator, JdbcTemplate jdbcTemplate) {
        this.validator = validator;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> getAllFilm() {
        String query = "SELECT * FROM FILMS";
        log.info("Все фильмы получены");
        return jdbcTemplate.query(
                query,
                new FilmMapper());
    }

    @Override
    public Film addFilm(Film film) throws ValidationException {
        validator.validateFilm(film);
        String queryAddFilm = "INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION) " +
                "VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(queryAddFilm, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration());
        log.info("Значения в таблицу FILMS внесены");

        String queryForReturnFilm = "SELECT * FROM FILMS WHERE DESCRIPTION = ?";
        Film film1 = jdbcTemplate.query(queryForReturnFilm,
                        new FilmMapper(),
                        film.getDescription())
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Ошибка вставки"));

        String queryAddRatingFilm = "INSERT INTO FILM_RATING(FILM_ID, RATING_ID) VALUES (?, ?)";
        jdbcTemplate.update(queryAddRatingFilm, film1.getId(), film.getMpa().getId());
        log.info("Значения в таблицу FILM_RATING внесены");

        if (film.getGenres() != null) {
            List<Genre> genres = new ArrayList<>(film.getGenres());
            for (Genre genre : genres) {
                String queryAddGenreFilm = "INSERT INTO FILM_GENRE(FILM_ID, GENRE_ID) VALUES (?, ?)";
                jdbcTemplate.update(queryAddGenreFilm, film1.getId(), genre.getId());
            }
            log.info("Значения в таблицу FILM_GENRE внесены");
        }
        film.setId(film1.getId());
        log.info("Фильм с id = {} добавлен", film.getId());
        return film;
    }

    @Override
    public void deleteFilm(long id) throws NotFoundException {
        String queryCheckDateFilmById = "SELECT * FROM FILMS WHERE FILM_ID = ?";
        Film film = jdbcTemplate.query(
                        queryCheckDateFilmById,
                        new FilmMapper(),
                        id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Фильм с идентификатором " + id + " не найден."));
        String queryDeleteDataFilmById = "DELETE FROM FILMS WHERE FILM_ID = ?";
        jdbcTemplate.update(queryDeleteDataFilmById, id);
        log.info("Значения из таблицы FILMS удалены по id {}", id);

        String queryDeleteDateRatingBuId = "DELETE FROM FILM_RATING WHERE FILM_ID = ?";
        jdbcTemplate.update(queryDeleteDateRatingBuId, id);
        log.info("Значения из таблицы FILM_RATING удалены по id {}", id);

        String queryCheckDateGenreById = "SELECT GENRE_ID FROM FILM_GENRE WHERE FILM_ID = ?";
        List<Genre> genres = jdbcTemplate.query(
                queryCheckDateGenreById,
                new GenreMapper(),
                id);
        String queryDeleteDateGenreById = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(queryDeleteDateGenreById, id);
        log.info("Значения из таблицы FILM_GENRE удалены по id {}", id);
        log.info("Фильм с id = {} удален", id);
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException, NotFoundException {
        validator.validateFilm(film);
        String queryCheckDateFilmById = "SELECT * FROM FILMS WHERE FILM_ID = ?";
        Film film1 = jdbcTemplate.query(
                        queryCheckDateFilmById,
                        new FilmMapper(),
                        film.getId())
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Фильм с идентификатором " + film.getId() + " не найден."));

        String queryUpdateDataTableFilms = "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?,RELEASE_DATE = ?, " +
                "DURATION = ? WHERE FILM_ID = ?";
        jdbcTemplate.update(queryUpdateDataTableFilms, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getId());
        log.info("Значения из таблицы FILMS обновлены по id {}", film.getId());
        Rating rating = film.getMpa();
        String queryUpdateDataTableFilmRating = "UPDATE FILM_RATING SET RATING_ID = ? WHERE FILM_ID = ?";
        jdbcTemplate.update(queryUpdateDataTableFilmRating, rating.getId(), film.getId());

        if (film.getGenres() != null) {
            List<Genre> genres = new ArrayList<>(film.getGenres());
            for (Genre genre : genres) {
                String queryUpdateDataTableFilmGenre = "UPDATE  FILM_GENRE SET GENRE_ID = ? WHERE FILM_ID = ?";
                jdbcTemplate.update(queryUpdateDataTableFilmGenre, genre.getId(), film.getId());
            }
            log.info("Значения из таблицы FILM_GENRE обновлены по id {}", film.getId());
        }
        log.info("Фильм с id = {} обновлен", film.getId());
        return film;
    }

    @Override
    public Film getFilm(long id) throws NotFoundException {
        String queryGetDateFilm = "SELECT * FROM FILMS WHERE FILM_ID = ?";
        Film film = jdbcTemplate.query(
                        queryGetDateFilm,
                        new FilmMapper(),
                        id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Фильм с идентификатором " + id + " не найден."));
        log.info("Значения из таблицы FILMS получены по id {}", id);

        String queryGetDateRating = "SELECT RATING_ID FROM FILM_RATING WHERE FILM_ID = ?";
        Rating rating = jdbcTemplate.query(
                        queryGetDateRating,
                        new RatingMapper(),
                        id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Фильм с идентификатором " + id + " не найден."));
        log.info("Значения из таблицы FILM_RATING получены по id {}", id);

        String queryGetDateGenre = "SELECT GENRE_ID FROM FILM_GENRE WHERE FILM_ID = ?";
        List<Genre> genres = jdbcTemplate.query(
                queryGetDateGenre,
                new GenreMapper(),
                id);
        log.info("Значения из таблицы FILM_GENRE получены по id {}", id);
        film.setMpa(rating);
        film.setGenres(genres);
        log.info("Фильм с идентификатором {} найден.", id);
        return film;
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
}
