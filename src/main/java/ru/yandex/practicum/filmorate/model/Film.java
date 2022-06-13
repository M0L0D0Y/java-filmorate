package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int likes;
    private Set<Long> idUsersWhoLiked = new HashSet<>();
    private long id;
    @NotEmpty(message = "Нет названия фильма")
    private final String name;
    @NotEmpty(message = "Нет описания фильма")
    private final String description;
    @Past(message = "Дата релиза не может быть в будущем")
    private final LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма не может быть отрицательной")
    private final int duration;
}
