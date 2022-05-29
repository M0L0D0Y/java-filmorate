package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class UserController {
    private static final String EMPTY_STRING = "";
    private static final String DOG_SYMBOL = "@";
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping("/users")
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping(value = "/users")
    public User addUser(@Valid @RequestBody User user) throws ValidationException {
        validationUser(user);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping(value = "/users")
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        if (user.getId() <= 0) {
            throw new ValidationException("id меньше или равен нулю");
        }
        validationUser(user);
        users.put(user.getId(), user);
        return user;
    }

    private void validationUser(User user) throws ValidationException {
        if ((user.getEmail() == null) || (!(user.getEmail().contains(DOG_SYMBOL)))) {
            throw new ValidationException("Неправильный формат почты");
        }
        if ((user.getLogin() == null) || (EMPTY_STRING.equals(user.getLogin()))) {
            throw new ValidationException("Неправильный формат логина");
        }
        if ((user.getName() == null) || (EMPTY_STRING.equals(user.getName()))) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
