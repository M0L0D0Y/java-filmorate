package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController

public class FilmController {
    private static final LocalDate DATE_RELEASE = LocalDate.of(1895, 12, 28);
    private static final int LINE_LENGTH = 201;
    private static final String EMPTY_STRING = "";
    private final Map<Long, Film> films = new HashMap<>();


    @GetMapping("/films")
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @PostMapping(value = "/films")
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        validationFilm(film);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        if (film.getId() <= 0) {
            throw new ValidationException("id меньше или равен нулю " + film.getId());
        }
        validationFilm(film);
        films.put(film.getId(), film);
        return film;
    }

    private void validationFilm(Film film) throws ValidationException {
        if (film.getName() == null || EMPTY_STRING.equals(film.getName())) {
            throw new ValidationException("Нет названия фильма " + film.getName());
        }
        if (film.getDescription().length() > LINE_LENGTH) {
            throw new ValidationException("Длинна описания фильма слишком большая " + film.getDescription().length());
        }
        if (film.getReleaseDate().isBefore(DATE_RELEASE)) {
            throw new ValidationException("Дата релиза перед " + DATE_RELEASE);
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма меньше нуля " + film.getDuration());
        }
    }
}
