package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Grades;

import java.util.List;

public interface PopularFilmsStorage {
    void addLikeFilm(long filmId, long userId, int grade);

    void deleteLike(long filmId, long userId);

    List<Film> getMostPopularFilms(long count);

    Grades getGrades(long filmId);

}
