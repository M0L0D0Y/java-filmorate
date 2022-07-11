package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendshipStorage {
    FriendshipStatus getStatusFriendship(long userId, long friendId);

    void updateStatusFriendship(long userId, long friendId, FriendshipStatus value);

    void addFriendship(long userId, long friendId);

    void deleteFriendship(long id, long friendId);

    List<User> getListFriend(long id);

    List<User> getCommonUsers(long id, long friendId);
}
