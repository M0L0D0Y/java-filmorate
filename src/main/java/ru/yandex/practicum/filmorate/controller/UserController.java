package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
public class UserController {
    private final UserService userService;
    private final UserStorage memoryUserStorage;

    @Autowired
    public UserController(UserService userService, @Qualifier("DatabaseUserStorage") UserStorage memoryUserStorage) {
        this.userService = userService;
        this.memoryUserStorage = memoryUserStorage;
    }

    @GetMapping("/users")
    public Collection<User> getAllUsers() {
        return memoryUserStorage.getAllUser();
    }

    @GetMapping(value = "/users/{id}")
    public User getUser(@PathVariable long id) {
        return memoryUserStorage.getUser(id);
    }

    @GetMapping(value = "/users/{id}/friends")
    public List<User> getListFriend(@PathVariable long id) {
        return userService.getListFriend(id);
    }

    @GetMapping(value = "/users/{id}/friends/common/{otherId}")
    public List<User> getCommonUsers(@PathVariable long id, @PathVariable long otherId) {
        return userService.getCommonUsers(id, otherId);
    }

    @PostMapping(value = "/users")
    public User addUser(@Valid @RequestBody User user) {
        return memoryUserStorage.addUser(user);
    }

    @PutMapping(value = "/users")
    public User updateUser(@Valid @RequestBody User user) {
        return memoryUserStorage.updateUser(user);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping(value = "/users")
    public void deleteUser(long id) {
        memoryUserStorage.deleteUser(id);
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        userService.deleteFriend(id, friendId);
    }
}
