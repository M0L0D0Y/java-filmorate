package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ValidatorTest {
    private static final String EMPTY_STRING = "";
    private static final String EMAIL_SYMBOL = "@";
    private static Validator validator;
    private static final LocalDate DATE_RELEASE = LocalDate.of(1895, 12, 28);
    private static final int LINE_LENGTH = 201;

    private static void validationUser(User user) throws ValidationException {
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

    private static void validationFilm(Film film) throws ValidationException {
        if (film.getName() == null || EMPTY_STRING.equals(film.getName())) {
            throw new ValidationException("Нет названия фильма " + film.getName());
        }
        if (film.getDescription().length() > LINE_LENGTH) {
            throw new ValidationException("Длинна описания фильма слишком большая " +film.getDescription().length());
        }
        if (film.getReleaseDate().isBefore(DATE_RELEASE)) {
            throw new ValidationException("Дата релиза перед " + DATE_RELEASE);
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма меньше нуля " + film.getDuration());
        }
    }

    @BeforeAll
    static void createValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void createUserWithNullName() throws ValidationException {
        User user = new User("test@mail.ru", "login", LocalDate.of(1990, 12, 15));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size());
        validationUser(user);
        assertEquals("login", user.getName());
    }

    @Test
    void createUserWithNullLogin() {
        User user = new User("test@mail.ru", "", LocalDate.of(1990, 12, 15));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Логин не должен быть пустым", violations.iterator().next().getMessage());
        assertThrows(ValidationException.class, () -> validationUser(user), "Неправильный формат логина");
    }

    @Test
    void createUserWithIncorrectEmail() {
        User user = new User("test_mail.ru", "login", LocalDate.of(1990, 12, 15));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Почта должна быть правильного формата", violations.iterator().next().getMessage());
        assertThrows(ValidationException.class, () -> validationUser(user), "Неправильный формат почты");
    }

    @Test
    void createUserWithIncorrectBirthday() {
        User user = new User("test@mail.ru", "login", LocalDate.of(2200, 12, 15));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Дата рождения должна быть в прошлом", violations.iterator().next().getMessage());
        assertThrows(ValidationException.class, () -> validationUser(user),
                "Дата рождения не может быть в будущем");
    }

    @Test
    void createFilm() {
        Film film = new Film("testName", "testDescription",
                LocalDate.of(1990, 12, 15), 120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size());
    }

    @Test
    void createFilmWithNullName() {
        Film film = new Film("", "testDescription",
                LocalDate.of(1990, 12, 15), 120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Нет названия фильма", violations.iterator().next().getMessage());
        assertThrows(ValidationException.class, () -> validationFilm(film),
                "Нет названия фильма");
    }

    @Test
    void createFilmWithNullDescription() {
        Film film = new Film("testName", "",
                LocalDate.of(1990, 12, 15), 120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Нет описания фильма", violations.iterator().next().getMessage());
    }

    @Test
    void createFilmWithIncorrectDescription() {
        Film film = new Film("testName", "Через некоторое время мы обнаружили, " +
                "что наш контент-менеджер обладает повышенным количеством свободного времени и " +
                "решили поручить ему выполнение дополнительной работы. Это повысило ему заработную " +
                "плату и помогло уменьшить нагрузку коллектива вцелом. В итоге счастлив " +
                "контент-менеджер и радуется вся команда!",
                LocalDate.of(1990, 12, 15), 120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size());
        assertThrows(ValidationException.class, () -> validationFilm(film),
                "Длинна описания фильма слишком большая");
    }

    @Test
    void createFilmWithReleaseDateInPats() {
        Film film = new Film("testName", "testDescription",
                LocalDate.of(1120, 12, 15), 120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size());
        assertThrows(ValidationException.class, () -> validationFilm(film),
                "Дата релиза перед " + DATE_RELEASE);
    }

    @Test
    void createFilmWithReleaseDateInFuture() {
        Film film = new Film("testName", "testDescription",
                LocalDate.of(2200, 12, 15), 120);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Дата релиза не может быть в будущем", violations.iterator().next().getMessage());
    }

    @Test
    void createFilmWithIncorrectDuration() {
        Film film = new Film("testName", "testDescription",
                LocalDate.of(1920, 12, 15), -1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Продолжительность фильма не может быть отрицательной", violations.iterator().next().getMessage());
        assertThrows(ValidationException.class, () -> validationFilm(film),
                "Продолжительность фильма меньше нуля " + film.getDuration());
    }
}