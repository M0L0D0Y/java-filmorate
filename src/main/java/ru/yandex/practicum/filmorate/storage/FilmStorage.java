package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getAllFilm();

    Film addFilm(Film film) throws ValidationException;

    void deleteFilm(long id) throws NotFoundException;

    Film updateFilm(Film film) throws ValidationException, NotFoundException;

    Film getFilm(long id) throws NotFoundException;
}
