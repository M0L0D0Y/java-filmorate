package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController

public class FilmController {
    private final FilmService filmService;
    private final FilmStorage memoryFilmStorage;

    @Autowired
    public FilmController(FilmService filmService, FilmStorage memoryFilmStorage) {
        this.filmService = filmService;
        this.memoryFilmStorage = memoryFilmStorage;
    }

    @GetMapping("/films")
    public Collection<Film> getAllFilms() {
        return memoryFilmStorage.getAllFilm();
    }

    @GetMapping(value = "/films/{id}")
    public void getFilm(@PathVariable long id) throws NotFoundException {
        memoryFilmStorage.getFilm(id);
    }

    @GetMapping(value = "/films/popular")
    private List<Film> getMostPopularFilms(
            @RequestParam(defaultValue = "10") long count) {
        return filmService.getMostPopularFilms(count);
    }

    @PostMapping(value = "/films")
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        return memoryFilmStorage.addFilm(film);
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException, NotFoundException {
        return memoryFilmStorage.updateFilm(film);
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public void addLikeFilm(@PathVariable long id, @PathVariable long userId) throws NotFoundException {
        filmService.addLikeFilm(id, userId);
    }

    @DeleteMapping(value = "/films")
    public void deleteFilm(@RequestParam long id) throws NotFoundException {
        memoryFilmStorage.deleteFilm(id);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable long id, @PathVariable long userId) throws NotFoundException {
        filmService.deleteLike(id, userId);
    }
}
