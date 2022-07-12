package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.mappers.RatingMapper;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component("DatabaseRatingStorage")
public class DatabaseRatingStorage implements RatingStorage {
    private final JdbcTemplate jdbcTemplate;

    public DatabaseRatingStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Rating> getAllRating() {
        String query = "SELECT * FROM RATING";
        List<Rating> ratings = new ArrayList<>(jdbcTemplate.query(
                query,
                new RatingMapper()));
        log.info("Все рейтинги получены");
        return ratings;
    }

    public Rating getRatingById(int ratingId) {
        String query = "SELECT * FROM RATING WHERE RATING_ID = ?";
        Rating rating = jdbcTemplate.query(query,
                        new RatingMapper(),
                        ratingId)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Рейтинг с идентификатором " + ratingId + " не найден."));
        log.info("Рейтинг с id = {} получен", ratingId);
        return rating;
    }
}
