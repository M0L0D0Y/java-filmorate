package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.Validator;
import ru.yandex.practicum.filmorate.service.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.service.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.service.mappers.RatingMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component("DatabaseFilmStorage")
public class DatabaseFilmStorage implements FilmStorage {

    private final Validator validator;
    private final JdbcTemplate jdbcTemplate;

    public DatabaseFilmStorage(Validator validator, JdbcTemplate jdbcTemplate) {
        this.validator = validator;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> getAllFilm() {
        List<Film> films = getAllDataFilms();
        for (Film film : films) {
            Rating rating = getAllRating(film);
            film.setMpa(rating);
        }
        for (Film film : films) {
            List<Genre> genres = getDateGenreById(film.getId());
            if (genres.size() != 0) {
                film.setGenres(genres);
            }
            film.setGenres(null);
        }
        return films;
    }

    @Override
    public Film addFilm(Film film) throws ValidationException {
        validator.validateFilm(film);
        addDataFilm(film);
        Film lastFilm = getLastFilm();
        addRatingFilm(film, lastFilm);
        addGenreFilm(film, lastFilm);
        addDataInTableFilmLikedUser(lastFilm);
        Film filmForReturn = getFilm(lastFilm.getId());
        log.info("Фильм с id = {} добавлен", filmForReturn.getId());
        return filmForReturn;
    }

    @Override
    public void deleteFilm(long id) throws NotFoundException {
        checkExistId(id);
        deleteDataFilmById(id);
        log.info("Фильм с id = {} удален", id);
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException, NotFoundException {
        validator.validateFilm(film);
        checkExistId(film.getId());
        updateDataTableFilms(film);
        updateDataTableFilmRating(film);
        updateDataTableFilmGenre(film);
        log.info("Фильм с id = {} обновлен", film.getId());
        return getFilm(film.getId());
    }

    @Override
    public Film getFilm(long id) throws NotFoundException {
        Film film = getDateFilmByID(id);
        Rating rating = getDateRatingById(id);
        film.setMpa(rating);
        List<Genre> genres = getDateGenreById(id);
        if (genres.size() > 0) {
            film.setGenres(genres);
        } else {
            List<Genre> EmptyListGenres = new ArrayList<>();
            film.setGenres(EmptyListGenres);
        }
        log.info("Фильм с идентификатором {} найден.", id);
        return film;
    }

    private List<Film> getAllDataFilms() {
        String queryGetAllDataFilms = "SELECT * FROM FILMS";
        List<Film> films = jdbcTemplate.query(
                queryGetAllDataFilms,
                new FilmMapper());
        log.info("Все фильмы получены");
        return films;
    }

    private Rating getAllRating(Film film) {
        String queryGetAllRating = "SELECT * FROM RATING WHERE RATING_ID IN(" +
                "SELECT RATING_ID FROM FILM_RATING WHERE FILM_ID = ?)";
        Rating rating = jdbcTemplate.query(
                        queryGetAllRating,
                        new RatingMapper(),
                        film.getId())
                .stream()
                .findAny()
                .orElse(null);
        log.info("Фильм {} имеет рейтинг {}", film.getId(), rating.getName());
        return rating;
    }

    private void addDataFilm(Film film) {
        String queryAddDataFilm = "INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION) " +
                "VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(queryAddDataFilm, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration());
        log.info("Значения в таблицу FILMS внесены");
    }

    private Film getLastFilm() {
        String queryForReturnFilm = "SELECT * FROM FILMS WHERE FILM_ID IN(SELECT MAX(FILM_ID) FROM FILMS)";
        Film film = jdbcTemplate.query(queryForReturnFilm,
                        new FilmMapper())
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Ошибка вставки"));
        log.info("Последний фильм получен");
        return film;
    }

    private void addRatingFilm(Film film, Film lastFilm) {
        String queryAddRatingFilm = "INSERT INTO FILM_RATING(FILM_ID, RATING_ID) VALUES (?, ?)";
        jdbcTemplate.update(queryAddRatingFilm, lastFilm.getId(), film.getMpa().getId());
        log.info("Значения в таблицу FILM_RATING внесены");
    }

    private void addGenreFilm(Film film, Film lastFilm) {
        if (film.getGenres() != null) {
            List<Genre> genres = new ArrayList<>(film.getGenres());
            for (Genre genre : genres) {
                String queryAddGenreFilm = "INSERT INTO FILM_GENRE(FILM_ID, GENRE_ID) VALUES (?, ?)";
                jdbcTemplate.update(queryAddGenreFilm, lastFilm.getId(), genre.getId());
            }
            log.info("Значения в таблицу FILM_GENRE внесены");
        } else {
            String queryAddGenreFilm = "INSERT INTO FILM_GENRE(FILM_ID, GENRE_ID) VALUES (?, ?)";
            jdbcTemplate.update(queryAddGenreFilm, lastFilm.getId(), null);
            log.info("Значения в таблицу FILM_GENRE внесены. Значение GENRE_ID = null");
        }
    }

    private void addDataInTableFilmLikedUser(Film filmForReturn) {
        String queryAddDataInTableFilmLikedUser = "INSERT INTO FILM_LIKED_USERS (FILM_ID, USER_ID) VALUES (?, ?)";
        jdbcTemplate.update(queryAddDataInTableFilmLikedUser, filmForReturn.getId(), null);
        log.info("Значения в таблицу FILM_LIKED_USERS внесены. USER_ID = null");
    }

    private void checkExistId(long id) throws NotFoundException {
        String queryCheckDateFilmById = "SELECT * FROM FILMS WHERE FILM_ID = ?";
        Film film = jdbcTemplate.query(
                        queryCheckDateFilmById,
                        new FilmMapper(),
                        id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Фильм с идентификатором " + id + " не найден."));
        log.info("Фильм с id {} существует", id);
    }

    private void deleteDataFilmById(long id) {
        String queryDeleteDataFilmById = "DELETE FROM FILMS WHERE FILM_ID = ?";
        jdbcTemplate.update(queryDeleteDataFilmById, id);
        log.info("Значения из таблицы FILMS удалены по id {}", id);
    }

    private void deleteDateRatingBuId(long id) {
        String queryDeleteDateRatingBuId = "DELETE FROM FILM_RATING WHERE FILM_ID = ?";
        jdbcTemplate.update(queryDeleteDateRatingBuId, id);
        log.info("Значения из таблицы FILM_RATING удалены по id {}", id);
    }

    private void deleteDateGenreById(long id) {
        List<Genre> genres = getDateGenreById(id);
        if (genres.size() > 0) {
            for (Genre genre : genres) {
                String queryDeleteDateGenreById = "DELETE FROM FILM_GENRE WHERE FILM_ID = ? AND GENRE_ID = ?";
                jdbcTemplate.update(queryDeleteDateGenreById, id, genre.getId());
            }
        } else {
            String queryDeleteDateGenreById = "DELETE FROM FILM_GENRE WHERE FILM_ID = ? AND GENRE_ID = ?";
            jdbcTemplate.update(queryDeleteDateGenreById, id, null);
        }

        log.info("Значения из таблицы FILM_GENRE удалены по id {}", id);
    }

    private void updateDataTableFilms(Film film) {
        String queryUpdateDataTableFilms = "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?,RELEASE_DATE = ?, " +
                "DURATION = ? WHERE FILM_ID = ?";
        jdbcTemplate.update(queryUpdateDataTableFilms, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getId());
        log.info("Значения из таблицы FILMS обновлены по id {}", film.getId());
        System.out.println();
    }

    private void updateDataTableFilmRating(Film film) {
        if (film.getMpa() != null) {
            Rating rating = film.getMpa();
            String queryUpdateDataTableFilmRating = "UPDATE FILM_RATING SET RATING_ID = ? WHERE FILM_ID = ?";
            jdbcTemplate.update(queryUpdateDataTableFilmRating, rating.getId(), film.getId());
            log.info("Значения из таблицы FILM_RATING обновлены по id {}", film.getId());
        } else {
            deleteDateRatingBuId(film.getId());
            log.info("Значения из таблицы FILM_RATING обновлены по id {}. Значение RATING_ID = null", film.getId());
        }
    }

    private void updateDataTableFilmGenre(Film film) {
        deleteDateGenreById(film.getId());
        if (film.getGenres() != null) {
            List<Genre> genres = new ArrayList<>(film.getGenres());
            for (Genre genre : genres) {
                String queryUpdateDataTableFilmGenre = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
                jdbcTemplate.update(queryUpdateDataTableFilmGenre, film.getId(), genre.getId());
            }
            log.info("Значения из таблицы FILM_GENRE обновлены по id {}", film.getId());
        } else {
            deleteDateGenreById(film.getId());
            log.info("Значения из таблицы FILM_GENRE обновлены по id {}. Значение GENRE_ID = null", film.getId());
        }
    }

    private Film getDateFilmByID(long id) {
        String queryGetDateFilmById = "SELECT * FROM FILMS WHERE FILM_ID = ?";
        Film film = jdbcTemplate.query(
                        queryGetDateFilmById,
                        new FilmMapper(),
                        id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Фильм с идентификатором " + id + " не найден."));
        log.info("Значения из таблицы FILMS получены по id {}", id);
        return film;
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
}
