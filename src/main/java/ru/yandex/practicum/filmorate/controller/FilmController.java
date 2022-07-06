package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
public class FilmController {
    private final FilmService filmService;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmController(FilmService filmService, @Qualifier("InDataBaseFilm") FilmStorage filmStorage) {
        this.filmService = filmService;
        this.filmStorage = filmStorage;
    }

    @GetMapping("/films")
    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilm();
    }

    @GetMapping(value = "/films/{id}")
    public Film getFilm(@PathVariable long id) throws NotFoundException {
        return filmStorage.getFilm(id);
    }

    @GetMapping(value = "/films/popular")
    public List<Film> getMostPopularFilms(
            @RequestParam(defaultValue = "10") long count) {
        return filmService.getMostPopularFilms(count);
    }

    @GetMapping(value = "/genres/{id}")
    public Genre getGenreById(@PathVariable int id) throws NotFoundException {
        return filmService.getGenreById(id);
    }

    @GetMapping(value = "/genres")
    public Collection<Genre> getAllGenres() throws NotFoundException {
        return filmService.getAllGenres();
    }

    @GetMapping(value = "/mpa/{id}")
    public Rating getRatingById(@PathVariable int id) throws NotFoundException {
        return filmService.getRatingById(id);
    }

    @GetMapping(value = "/mpa")
    public Collection<Rating> getAllRating() throws NotFoundException {
        return filmService.getAllRating();
    }

    @PostMapping(value = "/films")
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        return filmStorage.addFilm(film);
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException, NotFoundException {
        return filmStorage.updateFilm(film);
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public void addLikeFilm(@PathVariable long id, @PathVariable long userId) throws NotFoundException {
        filmService.addLikeFilm(id, userId);
    }

    @DeleteMapping(value = "/films")
    public void deleteFilm(@RequestParam long id) throws NotFoundException {
        filmStorage.deleteFilm(id);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId) throws NotFoundException {
        filmService.deleteLike(id, userId);
    }
}
