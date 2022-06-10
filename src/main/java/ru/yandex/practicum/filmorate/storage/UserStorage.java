package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

/*
 * в которых будут определены методы добавления, удаления и модификации объектов.*/
public interface UserStorage {
    Collection<User> getAllUser();

    User addUser(User user) throws ValidationException;

    void deleteUser(long id);

    User updateUser(User user) throws ValidationException;
}