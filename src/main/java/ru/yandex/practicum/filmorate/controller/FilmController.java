package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController

public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    private final FilmStorage memoryFilmStorage = InMemoryFilmStorage.getInMemoryFilmStorage();

    @GetMapping("/films")
    public Collection<Film> getAllFilms() {
        return memoryFilmStorage.getAllFilm();
    }

    @PostMapping(value = "/films")
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        return memoryFilmStorage.addFilm(film);
    }

    @DeleteMapping(value = "/films")
    public void deleteFilm(@RequestParam long id) throws ValidationException, NullPointerException  {
        memoryFilmStorage.deleteFilm(id);
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException, NullPointerException {
        return memoryFilmStorage.updateFilm(film);
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public void addLike(@PathVariable long id,
                        @PathVariable long userId) throws ValidationException, NullPointerException  {
        filmService.addLike(id, userId);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id,
                           @PathVariable long userId) throws ValidationException, NullPointerException  {
        filmService.deleteLike(id, userId);
    }

    @GetMapping(value = "/films/popular")
    private List<Film> getMostPopularFilms(
            @RequestParam(defaultValue = "10", required = false) long count) {
        return filmService.getMostPopularFilms(count);
    }
}
