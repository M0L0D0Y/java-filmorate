package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film {
    private long id;
    @NotEmpty(message = "Нет названия фильма")
    private final String name;
    @NotEmpty(message = "Нет описания фильма")
    private final String description;
    @Past(message = "Дата релиза не может быть в будущем")
    private final LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма не может быть отрицательной")
    private final int duration;
    @NotEmpty(message = "Нет жанра")
    private Set<Genre> genre = new HashSet<>();
    @NotEmpty(message = "Нет рейтинга")
    private Rating rating;
    private Set<Long> likedUsers = new HashSet<>();
}
