package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Validator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private static final InMemoryFilmStorage INSTANCE = new InMemoryFilmStorage();

    private final Map<Long, Film> films = new HashMap<>();
    private final Validator validator = Validator.getValidator();

    private InMemoryFilmStorage() {

    }

    @Override
    public Collection<Film> getAllFilm() {
        return films.values();
    }

    @Override
    public Film addFilm(Film film) throws ValidationException {
        validator.validateFilm(film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void deleteFilm(long id) {
        films.remove(id);
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException {
        if (film.getId() <= 0) {
            throw new ValidationException("id меньше или равен нулю " + film.getId());
        }
        validator.validateFilm(film);
        films.put(film.getId(), film);
        return film;
    }

    public Film getFilm(long id) {
        return films.get(id);
    }

    public static InMemoryFilmStorage getInMemoryFilmStorage() {
        return INSTANCE;
    }
}
