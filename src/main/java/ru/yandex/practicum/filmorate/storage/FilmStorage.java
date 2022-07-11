package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getAllFilm();

    Film addFilm(Film film);

    void deleteFilm(long id);

    Film updateFilm(Film film);

    Film getFilm(long id);
}
