package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film {
    private long id;
    @NotEmpty(message = "Нет названия фильма")
    private String name;
    @NotEmpty(message = "Нет описания фильма")
    private String description;
    @Past(message = "Дата релиза не может быть в будущем")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма не может быть отрицательной")
    private int duration;
    //@NotEmpty(message = "Нет рейтинга")
    private Rating mpa;
    //@NotEmpty(message = "Нет жанров")
    private List<Genre> genres;
}
