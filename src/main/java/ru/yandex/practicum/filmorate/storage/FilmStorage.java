package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

/*
* в которых будут определены методы добавления, удаления и модификации объектов.*/
public interface FilmStorage {
    Collection<Film> getAllFilm();

    Film addFilm(Film film) throws ValidationException;

    void deleteFilm(long id);

    Film updateFilm(Film film) throws ValidationException;
}
