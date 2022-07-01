package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class FilmRating {
    private long filmId;
    private Rating rating;
}
