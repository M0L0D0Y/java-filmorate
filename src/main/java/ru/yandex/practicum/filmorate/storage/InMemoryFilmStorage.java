package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Validator;
import ru.yandex.practicum.filmorate.service.FilmIdGenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
        film.setId(FilmIdGenerator.generate());
        validator.validateFilm(film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void deleteFilm(long id) throws NotFoundException {
        Set<Long> listIdFilm = films.keySet();
        if (!(listIdFilm.contains(id))) {
            throw new NotFoundException("Нет фильма с таким Id " + id);
        }
        films.remove(id);
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException, NotFoundException {
        Set<Long> listIdFilm = films.keySet();
        if (!(listIdFilm.contains(film.getId()))) {
            throw new NotFoundException("Нет фильма с таким Id " + film.getId());
        }
        validator.validateFilm(film);
        films.put(film.getId(), film);
        return film;
    }

    public Film getFilm(long id) throws NotFoundException {
        Set<Long> listIdFilm = films.keySet();
        if (!(listIdFilm.contains(id))) {
            throw new NotFoundException("Нет фильма с таким Id " + id);
        }
        return films.get(id);
    }

    public static InMemoryFilmStorage getInMemoryFilmStorage() {
        return INSTANCE;
    }
}
