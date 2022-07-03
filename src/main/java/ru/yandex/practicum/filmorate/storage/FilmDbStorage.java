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

import java.util.Collection;
@Component("InDataBaseFilm")
public class FilmDbStorage implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);

    private final Validator validator;
    private final FilmIdGenerator filmIdGenerator;
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(Validator validator, FilmIdGenerator filmIdGenerator, JdbcTemplate jdbcTemplate) {
        this.validator = validator;
        this.filmIdGenerator = filmIdGenerator;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> getAllFilm() {
        String query = "SELECT * FROM 'films'";
        log.info("Все фильмы получены");
        return jdbcTemplate.query(
                query,
                new BeanPropertyRowMapper<>(Film.class));
    }

    @Override
    public Film addFilm(Film film) throws ValidationException {
        validator.validateFilm(film);
        film.setId(filmIdGenerator.generate());
        String query = "INSERT INTO 'films' VALUES(?, ?, ?, ?, ?)";
        jdbcTemplate.update(query, film.getId(), film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration());
        log.info("Фильм с id = {} добавлен", film.getId());
        return film;
    }

    @Override
    public void deleteFilm(long id) throws NotFoundException {//TODO МДЕЛАТЬ ПРОВЕРКУ НА СУЩЕСТВОВАНИЕ ID
        String query = "DELETE  FROM 'films' WHERE 'film_id' = ?";
        jdbcTemplate.update(query, id);
        log.info("Фильм с id = {} удален", id);
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException, NotFoundException {
        validator.validateFilm(film);
        String query = "UPDATE 'films' SET film_name=?, film_description=?,film_releaseDate=?, " +
                "film_duration=? WHERE user_id=?";
        jdbcTemplate.update(query, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration());
        log.info("Фильм с id = {} обновлен", film.getId());
        return film;
    }

    @Override
    public Film getFilm(long id) throws NotFoundException {
        String query = "SELECT * FROM 'films' WHERE 'user_id' = ?";
        Film film = jdbcTemplate.query(
                        query,
                        new Object[]{id},
                        new BeanPropertyRowMapper<>(Film.class))
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Фильм с идентификатором " + id + " не найден."));
        log.info("Фильм с идентификатором {} найден.", id);
        return film;
    }
    public Collection<Genre> getAllGenres(){
        String query = "SELECT * FROM 'genres'";
        log.info("Все жанры получены");
        return jdbcTemplate.query(
                query,
                new BeanPropertyRowMapper<>(Genre.class));
    }
    public Genre getGenreById(int genreID){
        String query = "SELECT * FROM 'genres' WHERE 'genres_id' = ?";
        log.info("Жанр с id = {} удален", genreID);
        Genre genre = jdbcTemplate.query(query,
                new Object[]{genreID},
                new BeanPropertyRowMapper<>(Genre.class))
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Жанр с идентификатором " + genreID + " не найден."));
        log.info("Жанр с id = {} удален", genreID);
        return genre;
    }

    public Collection<Rating> getAllRating(){
        String query = "SELECT * FROM 'rating'";
        log.info("Все рейтинги получены");
        return jdbcTemplate.query(
                query,
                new BeanPropertyRowMapper<>(Rating.class));
    }
    public Rating getRatingById(int ratingId){
        String query = "SELECT * FROM 'rating' WHERE 'rating_id' = ?";
        Rating rating = jdbcTemplate.query(query,
                        new Object[]{ratingId},
                        new BeanPropertyRowMapper<>(Rating.class))
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Жанр с идентификатором " + ratingId + " не найден."));
        log.info("Рейтинг с id = {} получен", ratingId);
        return rating;
    }
}
