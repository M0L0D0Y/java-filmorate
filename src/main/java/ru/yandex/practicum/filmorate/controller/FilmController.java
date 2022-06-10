package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController

public class FilmController {
    private final InMemoryFilmStorage memoryFilmStorage = new InMemoryFilmStorage();

    @GetMapping("/films")
    public Collection<Film> getAllFilms() {
        return memoryFilmStorage.getAllFilm();
    }

    @PostMapping(value = "/films")
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        return memoryFilmStorage.addFilm(film);
    }
    @DeleteMapping(value = "/films")
    public void deleteFilm(@RequestParam long id){
        memoryFilmStorage.deleteFilm(id);
    }

    @PutMapping(value = "/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        return memoryFilmStorage.updateFilm(film);
    }


}
