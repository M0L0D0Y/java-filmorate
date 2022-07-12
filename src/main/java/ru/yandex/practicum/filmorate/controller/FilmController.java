package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
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

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping(value = "/films/{id}")
    public Film getFilm(@PathVariable long id) {
        return filmService.getFilmById(id);
    }

    @GetMapping(value = "/films/popular")
    public List<Film> getMostPopularFilms(
            @RequestParam(defaultValue = "10") long count) {
        return filmService.getMostPopularFilms(count);
    }

    @GetMapping(value = "/genres/{id}")
    public Genre getGenreById(@PathVariable int id) {
        return filmService.getGenreById(id);
    }

    @GetMapping(value = "/genres")
    public Collection<Genre> getAllGenres() {
        return filmService.getAllGenres();
    }

    @GetMapping(value = "/mpa/{id}")
    public Rating getRatingById(@PathVariable int id) {
        return filmService.getRatingById(id);
    }

    @GetMapping(value = "/mpa")
    public Collection<Rating> getAllRating() {
        return filmService.getAllRating();
    }

    @PostMapping(value = "/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public void addLikeFilm(@PathVariable long id, @PathVariable long userId) {
        filmService.addLikeFilm(id, userId);
    }

    @DeleteMapping(value = "/films")
    public void deleteFilm(@RequestParam long id) {
        filmService.deleteFilm(id);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId) {
        filmService.deleteLike(id, userId);
    }
}
