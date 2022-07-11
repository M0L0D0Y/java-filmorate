package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.mappers.GenreMapper;

import java.util.Collection;

@Slf4j
@Component("DatabaseGenreStorage")
public class DatabaseGenreStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public DatabaseGenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
}
