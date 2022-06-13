package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmComparator;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage memoryFilmStorage;
    private final UserStorage memoryUserStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage memoryFilmStorage, InMemoryUserStorage memoryUserStorage) {
        this.memoryFilmStorage = memoryFilmStorage;
        this.memoryUserStorage = memoryUserStorage;
    }

    public void addLikeFilm(long filmId, long userId) throws NotFoundException {
        Film film = memoryFilmStorage.getFilm(filmId);
        User user = memoryUserStorage.getUser(userId);//для проверки существования такого id пользователя
        Set<Long> UsersWhoLiked = film.getIdUsersWhoLiked();
        UsersWhoLiked.add(userId);
        film.setIdUsersWhoLiked(UsersWhoLiked);
        film.setLikes(UsersWhoLiked.size());
        //TODO не ставятся лайки при первом проходе теста
    }

    public void deleteLike(long filmId, long userId) throws NotFoundException {
        Film film = memoryFilmStorage.getFilm(filmId);
        Set<Long> UsersWhoLiked;
        UsersWhoLiked = film.getIdUsersWhoLiked();
        if (!(UsersWhoLiked.contains(userId))) {
            throw new NotFoundException("нет пользователя с таким Id " + userId);
        }
        UsersWhoLiked.remove(userId);
        film.setIdUsersWhoLiked(UsersWhoLiked);
        film.setLikes(UsersWhoLiked.size());
        //TODO скорее всего не удаляются лайки
    }

    public List<Film> getMostPopularFilms(long count) {
        FilmComparator comparator = new FilmComparator();
        List<Film> filmList = new LinkedList<>(memoryFilmStorage.getAllFilm());
        return filmList.stream().sorted(comparator)
                .limit(count)
                .collect(Collectors.toList());
        //TODO не сортируется список
    }
}
