package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final PopularFilmsStorage popularFilmsStorage;
    private final GenreStorage genreStorage;
    private final RatingStorage ratingStorage;

    @Autowired
    public FilmService(@Qualifier("DatabaseFilmStorage") FilmStorage filmStorage,
                       @Qualifier("DatabaseUserStorage") UserStorage userStorage,
                       @Qualifier("DatabasePopularFilmsStorage") PopularFilmsStorage popularFilmsStorage,
                       @Qualifier("DatabaseGenreStorage") GenreStorage genreStorage,
                       @Qualifier("DatabaseRatingStorage") RatingStorage ratingStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.popularFilmsStorage = popularFilmsStorage;
        this.genreStorage = genreStorage;
        this.ratingStorage = ratingStorage;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilm();
    }

    public Film getFilmById(long id) {
        return filmStorage.getFilm(id);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public void deleteFilm(long id) {
        filmStorage.deleteFilm(id);
    }

    public void addLikeFilm(long filmId, long userId) {
        checkExistId(filmId, userId);
        popularFilmsStorage.addLikeFilm(filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        checkExistId(filmId, userId);
        popularFilmsStorage.deleteLike(filmId, userId);
    }

    public List<Film> getMostPopularFilms(long count) {
        List<Film> updateFilms = new LinkedList<>();
        List<Film> films = popularFilmsStorage.getMostPopularFilms(count);
        for (Film film : films) {
            film = filmStorage.getFilm(film.getId());
            updateFilms.add(film);
        }
        return updateFilms;
    }

    public Collection<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    public Genre getGenreById(int genreID) {
        return genreStorage.getGenreById(genreID);
    }

    public List<Rating> getAllRating() {
        return ratingStorage.getAllRating();
    }

    public Rating getRatingById(int ratingId) {
        return ratingStorage.getRatingById(ratingId);
    }

    private void checkExistId(long filmId, long userId) {//для проверки существования таких id
        Film film = filmStorage.getFilm(filmId);
        User user = userStorage.getUser(userId);
    }

}
