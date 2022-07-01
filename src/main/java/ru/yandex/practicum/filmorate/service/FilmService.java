package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
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
    public FilmService(@Qualifier("InDataBaseFilm") FilmStorage memoryFilmStorage,
                       @Qualifier("InDataBaseUser") UserStorage memoryUserStorage) {
        this.memoryFilmStorage = memoryFilmStorage;
        this.memoryUserStorage = memoryUserStorage;
    }

    public void addLikeFilm(long filmId, long userId) throws NotFoundException {
        Film film = memoryFilmStorage.getFilm(filmId);
        User user = memoryUserStorage.getUser(userId);//для проверки существования такого id пользователя
        Set<Long> usersWhoLiked = film.getLikedUsers();
        usersWhoLiked.add(userId);
        film.setLikedUsers(usersWhoLiked);
    }

    public void deleteLike(long filmId, long userId) throws NotFoundException {
        Film film = memoryFilmStorage.getFilm(filmId);
        Set<Long> usersWhoLiked = film.getLikedUsers();
        if (!(usersWhoLiked.contains(userId))) {
            throw new NotFoundException("нет пользователя с таким Id " + userId);
        }
        usersWhoLiked.remove(userId);
        film.setLikedUsers(usersWhoLiked);
    }

    public List<Film> getMostPopularFilms(long count) {
        List<Film> filmList = new LinkedList<>(memoryFilmStorage.getAllFilm());
        filmList.sort(Comparator.comparingInt(o -> o.getLikedUsers().size() * (-1)));//reversed() почему-то не работает
        return filmList.stream()
                .limit(count)
                .collect(Collectors.toList());
    }
}
