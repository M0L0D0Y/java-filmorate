package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Component
public class Validator {
    private static final LocalDate DATE_RELEASE = LocalDate.of(1895, 12, 28);
    private static final int LINE_LENGTH = 201;
    private static final String EMPTY_STRING = "";
    private static final String SPACE_STRING = " ";
    private static final String EMAIL_SYMBOL = "@";


    public void validateFilm(Film film) {
        if (film.getName() == null || EMPTY_STRING.equals(film.getName())) {
            throw new ValidationException("Нет названия фильма " + film.getName());
        }
        if (film.getDescription() == null) {
            throw new ValidationException("Нет описания фильма");
        }
        if (film.getDescription().length() > LINE_LENGTH) {
            throw new ValidationException("Длинна описания фильма слишком большая " + film.getDescription().length());
        }
        if (film.getReleaseDate().isBefore(DATE_RELEASE)) {
            throw new ValidationException("Дата релиза перед " + DATE_RELEASE);
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма меньше нуля " + film.getDuration());
        }
        if (film.getMpa() == null) {
            throw new ValidationException("Нет рейтинга фильма ");
        }
    }

    public void validateUser(User user) {
        if ((user.getEmail() == null) || (!(user.getEmail().contains(EMAIL_SYMBOL)))) {
            throw new ValidationException("Неправильный формат почты " + user.getEmail());
        }
        if ((user.getLogin() == null) || (EMPTY_STRING.equals(user.getLogin()))
                || (user.getLogin().contains(SPACE_STRING))) {
            throw new ValidationException("Неправильный формат логина " + user.getLogin());
        }
        if ((user.getName() == null) || (EMPTY_STRING.equals(user.getName()))) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем " + user.getBirthday());
        }
    }
}
