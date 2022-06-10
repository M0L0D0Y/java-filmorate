package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmComparator;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.*;
import java.util.stream.Collectors;

/*
 * операции с фильмами, — добавление и удаление лайка,
 * вывод 10 наиболее популярных фильмов по количеству лайков. */
@Service
public class FilmService {

    private final InMemoryFilmStorage memoryFilmStorage = InMemoryFilmStorage.getInMemoryFilmStorage();
    private Set<Long> listUsers = new HashSet<>();

    public void addLike(long filmId, long userId) {
        Film film = memoryFilmStorage.getFilm(filmId);
        listUsers = film.getIdUsersWhoLiked();
        listUsers.add(userId);
        film.setIdUsersWhoLiked(listUsers);
        film.setLikes(listUsers.size());
        listUsers.clear();
    }

    public void deleteLike(long filmId, long userId) {
        Film film = memoryFilmStorage.getFilm(filmId);
        listUsers = film.getIdUsersWhoLiked();
        listUsers.remove(userId);
        film.setIdUsersWhoLiked(listUsers);
        film.setLikes(listUsers.size());
        listUsers.clear();
    }

    private List<Film> getMostPopularFilms(long count) {
        FilmComparator comparator= new FilmComparator();
        List<Film> filmList = new LinkedList<>(memoryFilmStorage.getAllFilm());
        return filmList.stream().sorted(comparator)
                .limit(count)
                .collect(Collectors.toList());

    }
}
