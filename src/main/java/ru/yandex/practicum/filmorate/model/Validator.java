package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

public class Validator {

    private static final Validator INSTANCE  = new Validator();
    private static final LocalDate DATE_RELEASE = LocalDate.of(1895, 12, 28);
    private static final int LINE_LENGTH = 201;
    private static final String EMPTY_STRING = "";
    private static final String EMAIL_SYMBOL = "@";

    private Validator() {
    }

    public void validateFilm(Film film) throws ValidationException {
        if (film.getName() == null || EMPTY_STRING.equals(film.getName())) {
            throw new ValidationException("Нет названия фильма " + film.getName());
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
    }

    public void validateUser(User user) throws ValidationException {
        if ((user.getEmail() == null) || (!(user.getEmail().contains(EMAIL_SYMBOL)))) {
            throw new ValidationException("Неправильный формат почты " + user.getEmail());
        }
        if ((user.getLogin() == null) || (EMPTY_STRING.equals(user.getLogin()))) {
            throw new ValidationException("Неправильный формат логина " + user.getLogin());
        }
        if ((user.getName() == null) || (EMPTY_STRING.equals(user.getName()))) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем " + user.getBirthday());
        }
    }

    public static Validator getValidator() {
        return INSTANCE ;
    }
}
