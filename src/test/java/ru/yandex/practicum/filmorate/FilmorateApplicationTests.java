package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private static final String EMAIL = "test@mail.ru";
    private static final String UPDATE_EMAIL = "update@mail.ru";
    private static final String FAIL_EMAIL = "failmail.ru";
    private static final String NAME = "testName";
    private static final String UPDATE_NAME = "updateName";
    private static final String LOGIN = "testLogin";
    private static final String UPDATE_LOGIN = "dolore";
    private static final String FAIL_LOGIN = "dolore ullamco";
    private static final LocalDate CORRECT_DATE = LocalDate.of(1990, 12, 15);
    private static final LocalDate FUTURE_DATE = LocalDate.of(3000, 12, 15);
    private static final LocalDate PAST_DATE = LocalDate.of(1800, 12, 15);
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmDbStorage;

    @Test
    public void testCreateAndGetUser() throws ValidationException {
        User userTest = new User();
        userTest.setEmail(EMAIL);
        userTest.setLogin(LOGIN);
        userTest.setName(NAME);
        userTest.setBirthday(CORRECT_DATE);
        userStorage.addUser(userTest);
        User user = userStorage.getUser(4L);
        assertEquals(userTest.getEmail(), user.getEmail());
        assertEquals(userTest.getName(), user.getName());
        assertEquals(userTest.getLogin(), user.getLogin());
        assertEquals(userTest.getBirthday(), user.getBirthday());
    }

    @Test
    public void testCreateUserWithFailEmail() {
        User userTest = new User();
        userTest.setEmail(FAIL_EMAIL);
        userTest.setLogin(LOGIN);
        userTest.setName(NAME);
        userTest.setBirthday(CORRECT_DATE);
        assertThrows(ValidationException.class, () -> userStorage.addUser(userTest),
                "Неправильный формат почты");
    }

    @Test
    public void testCreateUserWithFailLogin() {
        User userTest = new User();
        userTest.setEmail(EMAIL);
        userTest.setLogin(FAIL_LOGIN);
        userTest.setName(NAME);
        userTest.setBirthday(CORRECT_DATE);
        assertThrows(ValidationException.class, () -> userStorage.addUser(userTest),
                "Неправильный формат логина");
    }

    @Test
    public void testCreateUserWithFailBirthday() {
        User userTest = new User();
        userTest.setEmail(EMAIL);
        userTest.setLogin(LOGIN);
        userTest.setName(NAME);
        userTest.setBirthday(FUTURE_DATE);
        assertThrows(ValidationException.class, () -> userStorage.addUser(userTest),
                "Дата рождения должна быть в прошлом");
    }

    @Test
    public void testCreateUserWithNullName() throws ValidationException {
        User userTest = new User();
        userTest.setEmail(EMAIL);
        userTest.setLogin(LOGIN);
        userTest.setBirthday(CORRECT_DATE);
        userStorage.addUser(userTest);
        User user = userStorage.getUser(3L);
        assertEquals(userTest.getEmail(), user.getEmail());
        assertEquals(userTest.getName(), user.getName());
        assertEquals(userTest.getLogin(), user.getLogin());
        assertEquals(userTest.getBirthday(), user.getBirthday());
    }

    @Test
    public void testCreateUpdateAndGetUser() throws ValidationException {
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
    public void testCreateAndUpdateUserWithFailEmail() {
        User userTest = new User();
        userTest.setEmail(FAIL_EMAIL);
        userTest.setLogin(UPDATE_LOGIN);
        userTest.setName(UPDATE_NAME);
        userTest.setBirthday(PAST_DATE);
        assertThrows(ValidationException.class, () -> userStorage.updateUser(userTest),
                "Неправильный формат почты");
    }

    @Test
    public void testCreateAndUpdateUserWithFailLogin() {
        User updateUser = new User();
        updateUser.setId(1L);
        updateUser.setEmail(EMAIL);
        updateUser.setLogin(FAIL_LOGIN);
        updateUser.setName(UPDATE_NAME);
        updateUser.setBirthday(PAST_DATE);
        assertThrows(ValidationException.class, () -> userStorage.updateUser(updateUser),
                "Неправильный формат логина");
    }

    @Test
    public void testCreateAndUpdateUserWithFailBirthday() {
        User userTest = new User();
        userTest.setEmail(EMAIL);
        userTest.setLogin(LOGIN);
        userTest.setName(UPDATE_NAME);
        userTest.setBirthday(FUTURE_DATE);
        assertThrows(ValidationException.class, () -> userStorage.updateUser(userTest),
                "Дата рождения должна быть в прошлом");
    }

    @Test
    public void testCreateDeleteAndGetUser() throws ValidationException {
        User userTest = new User();
        userTest.setEmail(EMAIL);
        userTest.setLogin(LOGIN);
        userTest.setName(NAME);
        userTest.setBirthday(CORRECT_DATE);
        userStorage.addUser(userTest);
        User userForDeleteTest = new User();
        userForDeleteTest.setEmail(UPDATE_EMAIL);
        userForDeleteTest.setLogin(UPDATE_LOGIN);
        userForDeleteTest.setName(UPDATE_NAME);
        userForDeleteTest.setBirthday(PAST_DATE);
        userStorage.addUser(userForDeleteTest);
        userStorage.deleteUser(2L);
        List<User> users = new ArrayList<>(userStorage.getAllUser());
        assertEquals(users.size(), 1);
        User user = users.get(0);
        assertEquals(userTest.getEmail(), user.getEmail());
        assertEquals(userTest.getName(), user.getName());
        assertEquals(userTest.getLogin(), user.getLogin());
        assertEquals(userTest.getBirthday(), user.getBirthday());
    }
}
