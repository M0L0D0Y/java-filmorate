package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    private final UserStorage memoryUserStorage = InMemoryUserStorage.getInMemoryUserStorage();

    @GetMapping("/users")
    public Collection<User> getAllUsers() {
        return memoryUserStorage.getAllUser();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable long id) throws ValidationException, NotFoundException {
        return memoryUserStorage.getUser(id);
    }


    @PostMapping(value = "/users")
    public User addUser(@Valid @RequestBody User user) throws ValidationException {
        return memoryUserStorage.addUser(user);
    }

    @DeleteMapping(value = "/users/{id}")
    public void deleteUser(@PathVariable long id) throws ValidationException, NotFoundException {
        memoryUserStorage.deleteUser(id);
    }

    @PutMapping(value = "/users")
    public User updateUser(@Valid @RequestBody User user) throws ValidationException, NotFoundException {
        return memoryUserStorage.updateUser(user);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable long id,
                          @PathVariable long friendId) throws ValidationException, NotFoundException {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable long id,
                             @PathVariable long friendId) throws ValidationException, NotFoundException {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping(value = "/users/{id}/friends")
    public List<User> getListFriend(@PathVariable long id) throws ValidationException, NotFoundException {
        return userService.getListFriend(id);
    }

    @GetMapping(value = "/users/{id}/friends/common/{otherId}")
    public List<User> getCommonUsers(@PathVariable long id,
                                     @PathVariable long otherId) throws ValidationException, NotFoundException {
        return userService.getCommonUsers(id, otherId);
    }
}
