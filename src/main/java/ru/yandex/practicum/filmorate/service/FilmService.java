package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmComparator;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.*;
import java.util.stream.Collectors;

/*
 * операции с фильмами, — добавление и удаление лайка,
 * вывод 10 наиболее популярных фильмов по количеству лайков. */
@Service
public class FilmService {

    private final FilmStorage memoryFilmStorage;
    private Set<Long> listFilm = new HashSet<>();

    @Autowired
    public FilmService(InMemoryFilmStorage memoryFilmStorage) {
        this.memoryFilmStorage = memoryFilmStorage;
    }

    public void addLike(long filmId, long userId) throws NotFoundException {
        Film film = memoryFilmStorage.getFilm(filmId);
        listFilm = film.getIdUsersWhoLiked();
        listFilm.add(userId);
        film.setIdUsersWhoLiked(listFilm);
        film.setLikes(listFilm.size());
        listFilm.clear();
    }

    public void deleteLike(long filmId, long userId) throws NotFoundException {
        Film film = memoryFilmStorage.getFilm(filmId);
        listFilm = film.getIdUsersWhoLiked();
        if (!(listFilm.contains(userId))) {
            throw new NotFoundException("нет пользователя с таким Id " + userId);
        }
        listFilm.remove(userId);
        film.setIdUsersWhoLiked(listFilm);
        film.setLikes(listFilm.size());
        listFilm.clear();
    }

    public List<Film> getMostPopularFilms(long count) {
        FilmComparator comparator = new FilmComparator();
        List<Film> filmList = new LinkedList<>(memoryFilmStorage.getAllFilm());
        return filmList.stream().sorted(comparator)
                .limit(count)
                .collect(Collectors.toList());

    }
}
