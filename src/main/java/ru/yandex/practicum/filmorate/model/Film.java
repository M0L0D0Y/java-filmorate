package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.service.FilmIdGenerator;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {
    private final long likes;
    private final long id = FilmIdGenerator.generate();
    @NotEmpty(message = "Нет названия фильма")
    private final String name;
    @NotEmpty(message = "Нет описания фильма")
    private final String description;
    @Past(message = "Дата релиза не может быть в будущем")
    private final LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма не может быть отрицательной")
    private final int duration;
}
