package ru.yandex.practicum.filmorate.service.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RatingMapper implements RowMapper<Rating> {
    @Override
    public Rating mapRow(ResultSet rs, int rowNum) throws SQLException {
        Rating rating = new Rating();
        rating.setId(rs.getInt("RATING_ID"));
        rating.setName(rs.getString("NAME"));
        return rating;
    }
}
