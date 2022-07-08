package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private static final String EMAIL = "test@mail.ru";
    private static final String UPDATE_EMAIL = "update@mail.ru";
    private static final String NAME = "testName";
    private static final String UPDATE_NAME = "updateName";
    private static final String LOGIN = "testLogin";
    private static final String UPDATE_LOGIN = "dolore";
    private static final LocalDate CORRECT_DATE = LocalDate.of(1990, 12, 15);
    private static final LocalDate PAST_DATE = LocalDate.of(1800, 12, 15);
    private static final String DESCRIPTION = "testDescription";
    private static final int DURATION = 120;
    private static final int UPDATE_DURATION = 90;
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmDbStorage;

    @Test
    public void testUserCreateAndGet() throws ValidationException {
        User userTest = new User();
        userTest.setEmail(EMAIL);
        userTest.setLogin(LOGIN);
        userTest.setName(NAME);
        userTest.setBirthday(CORRECT_DATE);
        userStorage.addUser(userTest);
        User user = userStorage.getUser(1L);
        assertEquals(userTest.getEmail(), user.getEmail());
        assertEquals(userTest.getName(), user.getName());
        assertEquals(userTest.getLogin(), user.getLogin());
        assertEquals(userTest.getBirthday(), user.getBirthday());
    }

    @Test
    public void testUserUpdate() throws ValidationException {
        User user = new User();
        user.setId(1L);
        user.setEmail(UPDATE_EMAIL);
        user.setLogin(UPDATE_LOGIN);
        user.setName(UPDATE_NAME);
        user.setBirthday(PAST_DATE);
        userStorage.updateUser(user);
        User updateUser = userStorage.getUser(1L);
        assertEquals(user.getEmail(), updateUser.getEmail());
        assertEquals(user.getName(), updateUser.getName());
        assertEquals(user.getLogin(), updateUser.getLogin());
        assertEquals(user.getBirthday(), updateUser.getBirthday());
    }

    @Test
    public void testUserDeleteAndGetAll() throws ValidationException {
        User userTest = new User();
        userTest.setEmail(UPDATE_EMAIL);
        userTest.setLogin(UPDATE_LOGIN);
        userTest.setName(UPDATE_NAME);
        userTest.setBirthday(PAST_DATE);
        userStorage.addUser(userTest);
        userStorage.deleteUser(2L);
        List<User> users = new ArrayList<>(userStorage.getAllUser());
        assertEquals(users.size(), 1);
    }

    @Test
    public void testFilmCreateAndGet() throws ValidationException {
        Film filmTest = new Film();
        filmTest.setName(NAME);
        filmTest.setDescription(DESCRIPTION);
        filmTest.setDuration(DURATION);
        filmTest.setReleaseDate(CORRECT_DATE);
        Rating rating = new Rating();
        rating.setId(1);
        filmTest.setMpa(rating);
        filmDbStorage.addFilm(filmTest);
        Film film = filmDbStorage.getFilm(1L);
        assertEquals(filmTest.getName(), film.getName());
        assertEquals(filmTest.getDescription(), film.getDescription());
        assertEquals(filmTest.getDuration(), film.getDuration());
        assertEquals(filmTest.getReleaseDate(), film.getReleaseDate());
        assertEquals(filmTest.getMpa().getId(), film.getMpa().getId());
    }

    @Test
    public void testFilmUpdate() throws ValidationException {
        Film filmTest = new Film();
        filmTest.setId(1L);
        filmTest.setDescription(DESCRIPTION);
        filmTest.setDuration(UPDATE_DURATION);
        filmTest.setName(UPDATE_NAME);
        filmTest.setReleaseDate(CORRECT_DATE);
        Rating rating = new Rating();
        rating.setId(1);
        filmTest.setMpa(rating);
        filmDbStorage.updateFilm(filmTest);
        Film film = filmDbStorage.getFilm(1L);
        assertEquals(filmTest.getName(), film.getName());
        assertEquals(filmTest.getDescription(), film.getDescription());
        assertEquals(filmTest.getDuration(), film.getDuration());
        assertEquals(filmTest.getReleaseDate(), film.getReleaseDate());
        assertEquals(filmTest.getMpa().getId(), film.getMpa().getId());
    }

    @Test
    public void testFilmDeleteAndGetAll() throws ValidationException {
        Film filmTest = new Film();
        filmTest.setDescription(DESCRIPTION);
        filmTest.setDuration(DURATION);
        filmTest.setName(NAME);
        filmTest.setReleaseDate(CORRECT_DATE);
        Rating rating = new Rating();
        rating.setId(1);
        filmTest.setMpa(rating);
        filmDbStorage.addFilm(filmTest);
        filmDbStorage.deleteFilm(2L);
        List<Film> films = new ArrayList<>(filmDbStorage.getAllFilm());
        assertEquals(films.size(), 1);
    }
}
