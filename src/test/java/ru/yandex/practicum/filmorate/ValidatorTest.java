package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Validator;

import java.time.LocalDate;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ValidatorTest {
    private static final LocalDate DATE_RELEASE = LocalDate.of(1895, 12, 28);
    private static final String EMAIL = "test@mail.ru";
    private static final String FAIL_EMAIL = "failmail.ru";
    private static final String NAME = "testName";
    private static final String LOGIN = "testLogin";
    private static final String FAIL_LOGIN = "dolore ullamco";
    private static final LocalDate CORRECT_DATE = LocalDate.of(1990, 12, 15);
    private static final LocalDate FUTURE_DATE = LocalDate.of(3000, 12, 15);
    private static final LocalDate PAST_DATE = LocalDate.of(1800, 12, 15);
    private static final String DESCRIPTION = "testDescription";
    private static final String FAIL_DESCRIPTION = "Через некоторое время мы обнаружили," +
            "что наш контент-менеджер обладает повышенным количеством свободного времени и" +
            "решили поручить ему выполнение дополнительной работы. Это повысило ему заработную " +
            "плату и помогло уменьшить нагрузку коллектива вцелом. В итоге счастлив " +
            "контент-менеджер и радуется вся команда!";
    private static final int DURATION = 120;
    private static final int FAIL_DURATION = -1;

    private static final Validator validator = new Validator();

    @Test
    void createUserWithNullName() throws ValidationException {
        User user = new User();
        user.setBirthday(CORRECT_DATE);
        user.setEmail(EMAIL);
        user.setLogin(LOGIN);
        validator.validateUser(user);
        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    void createUserWithNullLogin() {
        User user = new User();
        user.setBirthday(CORRECT_DATE);
        user.setName(NAME);
        user.setEmail(EMAIL);
        user.setLogin(FAIL_LOGIN);
        assertThrows(ValidationException.class, () -> validator.validateUser(user),
                "Неправильный формат логина");
    }

    @Test
    void createUserWithIncorrectEmail() {
        User user = new User();
        user.setEmail(FAIL_EMAIL);
        user.setBirthday(CORRECT_DATE);
        user.setName(NAME);
        user.setLogin(LOGIN);
        assertThrows(ValidationException.class, () -> validator.validateUser(user),
                "Неправильный формат почты");
    }

    @Test
    void createUserWithIncorrectBirthday() {
        User user = new User();
        user.setBirthday(FUTURE_DATE);
        user.setName(NAME);
        user.setEmail(EMAIL);
        user.setLogin(LOGIN);
        assertThrows(ValidationException.class, () -> validator.validateUser(user),
                "Дата рождения должна быть в прошлом");
    }


    @Test
    void createFilmWithNullName() {
        Film film = new Film();
        film.setDuration(DURATION);
        film.setReleaseDate(CORRECT_DATE);
        film.setDescription(DESCRIPTION);
        assertThrows(ValidationException.class, () -> validator.validateFilm(film),
                "Нет названия фильма");
    }

    @Test
    void createFilmWithNullDescription() {
        Film film = new Film();
        film.setDuration(DURATION);
        film.setName(NAME);
        film.setReleaseDate(CORRECT_DATE);
        assertThrows(ValidationException.class, () -> validator.validateFilm(film),
                "Нет описания фильма");
    }

    @Test
    void createFilmWithIncorrectDescription() {
        Film film = new Film();
        film.setDescription(FAIL_DESCRIPTION);
        film.setDuration(DURATION);
        film.setName(NAME);
        film.setReleaseDate(CORRECT_DATE);
        assertThrows(ValidationException.class, () -> validator.validateFilm(film),
                "Длинна описания фильма слишком большая");
    }

    @Test
    void createFilmWithReleaseDateInPats() {
        Film film = new Film();
        film.setDescription(DESCRIPTION);
        film.setDuration(DURATION);
        film.setName(NAME);
        film.setReleaseDate(PAST_DATE);
        assertThrows(ValidationException.class, () -> validator.validateFilm(film),
                "Дата релиза перед " + DATE_RELEASE);
    }

    @Test
    void createFilmWithIncorrectDuration() {
        Film film = new Film();
        film.setDescription(DESCRIPTION);
        film.setDuration(FAIL_DURATION);
        film.setName(NAME);
        film.setReleaseDate(PAST_DATE);
        assertThrows(ValidationException.class, () -> validator.validateFilm(film),
                "Продолжительность фильма меньше нуля " + film.getDuration());
    }
    @Test
    void createFilmWithNullRating() {
        Film film = new Film();
        film.setDescription(DESCRIPTION);
        film.setDuration(FAIL_DURATION);
        film.setName(NAME);
        film.setReleaseDate(CORRECT_DATE);
        assertThrows(ValidationException.class, () -> validator.validateFilm(film),
                "Нет рейтинга фильма");
    }
}