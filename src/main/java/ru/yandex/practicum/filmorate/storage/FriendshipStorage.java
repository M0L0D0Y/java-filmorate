package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.StatusFriendship;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendshipStorage {
    StatusFriendship getStatusFriendship(long userId, long friendId);

    void updateStatusFriendship(long userId, long friendId, StatusFriendship value);

    void addFriendship(long userId, long friendId);

    void deleteFriendship(long id, long friendId);

    List<User> getListFriend(long id);

    List<User> getCommonUsers(long id, long friendId);
}
