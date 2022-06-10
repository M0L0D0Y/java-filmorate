package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
public class UserController {

    private final InMemoryUserStorage memoryUserStorage = new InMemoryUserStorage();

    @GetMapping("/users")
    public Collection<User> getAllUsers() {
        return memoryUserStorage.getAllUser();
    }

    @PostMapping(value = "/users")
    public User addUser(@Valid @RequestBody User user) throws ValidationException {
        return memoryUserStorage.addUser(user);
    }

    @DeleteMapping(value = "/users")
    public void deleteUser(long id) {
        memoryUserStorage.deleteUser(id);
    }

    @PutMapping(value = "/users")
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        return memoryUserStorage.updateUser(user);
    }

}
