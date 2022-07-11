package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.StatusFriendship;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    Collection<User> getAllUser();

    User addUser(User user);

    void deleteUser(long id);

    User updateUser(User user);

    User getUser(long id);


}
