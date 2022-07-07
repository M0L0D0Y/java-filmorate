package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private static final String emailTest = "test@mail.ru";
    private static final String failEmailTest = "testmail";
    private static final String nameTest = "testName";
    private static final String loginTest = "testLogin";
    private static final String failLoginTest = "dolore ullamco";
    private static final LocalDate birthdayTest = LocalDate.of(1990, 12, 15);
    private static final LocalDate failBirthdayTest = LocalDate.of(2446, 12, 15);
    private final UserDbStorage userStorage;

    @Test
    public void testCreateAndGetUser() throws ValidationException {
        User userTest = new User();
        userTest.setEmail(emailTest);
        userTest.setLogin(loginTest);
        userTest.setName(nameTest);
        userTest.setBirthday(birthdayTest);
        userStorage.addUser(userTest);
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUser(1L));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L));
    }

    @Test
    public void testCreateUserWithFailLogin() throws ValidationException {
        User userTest = new User();
        userTest.setEmail(emailTest);
        userTest.setLogin(failLoginTest);
        userTest.setName(nameTest);
        userTest.setBirthday(birthdayTest);
        userStorage.addUser(userTest);
        assertThrows(new ErrorResponse(), () -> userStorage.addUser(userTest),
                                "Неправильный формат почты");
    }
}
