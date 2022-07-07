package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
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
    private static final String emailTest = "test@mail.ru";
    private static final String updateEmail = "update@mail.ru";
    private static final String failEmail = "failmail.ru";
    private static final String nameTest = "testName";
    private static final String updateNameTest = "updateName";
    private static final String loginTest = "testLogin";
    private static final String updateLoginTest = "dolore";
    private static final String failLoginTest = "dolore ullamco";
    private static final LocalDate birthdayTest = LocalDate.of(1990, 12, 15);
    private static final LocalDate updateBirthdayTest = LocalDate.of(2000, 12, 15);
    private static final LocalDate failBirthdayTest = LocalDate.of(3000, 12, 15);
    private final UserDbStorage userStorage;

    @Test
    public void testCreateAndGetUser() throws ValidationException {
        User userTest = new User();
        userTest.setEmail(emailTest);
        userTest.setLogin(loginTest);
        userTest.setName(nameTest);
        userTest.setBirthday(birthdayTest);
        userStorage.addUser(userTest);
        User user = userStorage.getUser(3L);
        assertEquals(userTest.getEmail(), user.getEmail());
        assertEquals(userTest.getName(), user.getName());
        assertEquals(userTest.getLogin(), user.getLogin());
        assertEquals(userTest.getBirthday(), user.getBirthday());
    }

    @Test
    public void testCreateUserWithFailEmail() {
        User userTest = new User();
        userTest.setEmail(failEmail);
        userTest.setLogin(loginTest);
        userTest.setName(nameTest);
        userTest.setBirthday(birthdayTest);
        assertThrows(ValidationException.class, () -> userStorage.addUser(userTest),
                "Неправильный формат почты");
    }

    @Test
    public void testCreateUserWithFailLogin() {
        User userTest = new User();
        userTest.setEmail(emailTest);
        userTest.setLogin(failLoginTest);
        userTest.setName(nameTest);
        userTest.setBirthday(birthdayTest);
        assertThrows(ValidationException.class, () -> userStorage.addUser(userTest),
                "Неправильный формат логина");
    }

    @Test
    public void testCreateUserWithFailBirthday() {
        User userTest = new User();
        userTest.setEmail(emailTest);
        userTest.setLogin(loginTest);
        userTest.setName(nameTest);
        userTest.setBirthday(failBirthdayTest);
        assertThrows(ValidationException.class, () -> userStorage.addUser(userTest),
                "Дата рождения должна быть в прошлом");
    }

    @Test
    public void testCreateUserWithNullName() throws ValidationException {
        User userTest = new User();
        userTest.setEmail(emailTest);
        userTest.setLogin(loginTest);
        userTest.setBirthday(birthdayTest);
        userStorage.addUser(userTest);
        User user = userStorage.getUser(4L);
        assertEquals(userTest.getEmail(), user.getEmail());
        assertEquals(userTest.getName(), user.getName());
        assertEquals(userTest.getLogin(), user.getLogin());
        assertEquals(userTest.getBirthday(), user.getBirthday());
    }

    @Test
    public void testCreateUpdateAndGetUser() throws ValidationException {
        User userTest = new User();
        userTest.setEmail(emailTest);
        userTest.setLogin(loginTest);
        userTest.setName(nameTest);
        userTest.setBirthday(birthdayTest);
        userStorage.addUser(userTest);
        User user = userStorage.getUser(1L);
        assertEquals(userTest.getEmail(), user.getEmail());
        assertEquals(userTest.getName(), user.getName());
        assertEquals(userTest.getLogin(), user.getLogin());
        assertEquals(userTest.getBirthday(), user.getBirthday());
        user.setEmail(updateEmail);
        user.setLogin(updateLoginTest);
        user.setName(updateNameTest);
        user.setBirthday(updateBirthdayTest);
        userStorage.updateUser(user);
        User updateUser = userStorage.getUser(1L);
        assertEquals(user.getEmail(), updateUser.getEmail());
        assertEquals(user.getName(), updateUser.getName());
        assertEquals(user.getLogin(), updateUser.getLogin());
        assertEquals(user.getBirthday(), updateUser.getBirthday());
    }

    @Test
    public void testCreateAndUpdateUserWithFailEmail() throws ValidationException {
        User userTest = new User();
        userTest.setEmail(emailTest);
        userTest.setLogin(loginTest);
        userTest.setName(nameTest);
        userTest.setBirthday(birthdayTest);
        userStorage.addUser(userTest);
        userTest.setEmail(failEmail);
        userTest.setLogin(updateLoginTest);
        userTest.setName(updateNameTest);
        userTest.setBirthday(updateBirthdayTest);
        assertThrows(ValidationException.class, () -> userStorage.updateUser(userTest),
                "Неправильный формат почты");
    }

    @Test
    public void testCreateAndUpdateUserWithFailLogin() throws ValidationException {
        User userTest = new User();
        userTest.setEmail(emailTest);
        userTest.setLogin(loginTest);
        userTest.setName(nameTest);
        userTest.setBirthday(birthdayTest);
        userStorage.addUser(userTest);
        User updateUser = new User();
        updateUser.setId(1L);
        updateUser.setEmail(emailTest);
        updateUser.setLogin(failLoginTest);
        updateUser.setName(updateNameTest);
        updateUser.setBirthday(updateBirthdayTest);
        assertThrows(ValidationException.class, () -> userStorage.updateUser(updateUser),
                "Неправильный формат логина");
    }

    @Test
    public void testCreateAndUpdateUserWithFailBirthday() throws ValidationException {
        User userTest = new User();
        userTest.setEmail(emailTest);
        userTest.setLogin(loginTest);
        userTest.setName(nameTest);
        userTest.setBirthday(birthdayTest);
        userStorage.addUser(userTest);
        userTest.setEmail(emailTest);
        userTest.setLogin(loginTest);
        userTest.setName(updateNameTest);
        userTest.setBirthday(failBirthdayTest);
        assertThrows(ValidationException.class, () -> userStorage.updateUser(userTest),
                "Дата рождения должна быть в прошлом");
    }

    @Test
    public void testCreateDeleteAndGetUser() throws ValidationException {
        User userTest = new User();
        userTest.setEmail(emailTest);
        userTest.setLogin(loginTest);
        userTest.setName(nameTest);
        userTest.setBirthday(birthdayTest);
        userStorage.addUser(userTest);
        User userForDeleteTest = new User();
        userForDeleteTest.setEmail(updateEmail);
        userForDeleteTest.setLogin(updateLoginTest);
        userForDeleteTest.setName(updateNameTest);
        userForDeleteTest.setBirthday(updateBirthdayTest);
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
