package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.StatusFriendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.DatabaseFriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final DatabaseFriendshipStorage friendshipStorage;

    @Autowired
    public UserService(@Qualifier("DatabaseUserStorage") UserStorage userStorage,
                       DatabaseFriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
    }
    public Collection<User> getAllUsers() {
        return userStorage.getAllUser();
    }
    public User getUser(long id) {
        return userStorage.getUser(id);
    }
    public User addUser(User user) {
        return userStorage.addUser(user);
    }
    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }
    public void deleteUser(long id) {
        userStorage.deleteUser(id);
    }

    public void addFriend(long userId, long friendId) {
        if (userId == friendId) {
            log.info("Нельзя отправить запрос на дружбу самому себе. Одинаковые id {}={}", userId, friendId);
            return;
        }
        checkExistId(userId, friendId);
        StatusFriendship statusUserFriend = friendshipStorage.getStatusFriendship(userId, friendId);
        if (statusUserFriend == StatusFriendship.UNCONFIRMED) {
            log.info("Пользователь с id {} уже отправлял запрос на дружбу пользователю с id {}", userId, friendId);
        }
        if (statusUserFriend == StatusFriendship.CONFIRMED) {
            log.info("Пользователь с id {} уже дружит с пользователем с id {}", userId, friendId);
        }
        if (statusUserFriend == null) {
            log.info("Такой запрос делается впервые. Проверим на возможность подтвердить дружбу");
            StatusFriendship statusIdFriendUser = friendshipStorage.getStatusFriendship(friendId, userId);
            if (statusIdFriendUser == StatusFriendship.UNCONFIRMED) {
                friendshipStorage.updateStatusFriendship(userId, friendId,StatusFriendship.CONFIRMED);
                log.info("Пользователь с id {} подтвердил запрос дружбы пользователя с id {}", userId, friendId);
            }
            if (statusIdFriendUser == null) {
                friendshipStorage.addFriendship(userId, friendId);
                log.info("Пользователь с id {} отправил запрос на дружбу пользователю с id {}", userId, friendId);
            }
        }
    }

    public void deleteFriend(long id, long friendId) {
        if (id == friendId) {
            log.info("Нельзя удалить себя из друзей. Одинаковые id {}={}", id, friendId);
            return;
        }
        checkExistId(id, friendId);
        StatusFriendship status = friendshipStorage.getStatusFriendship(id, friendId);
        if (status == StatusFriendship.CONFIRMED) {
            friendshipStorage.updateStatusFriendship(friendId, id, StatusFriendship.UNCONFIRMED);
            log.info("Статус дружбы у бывшего друга обновлен");
            friendshipStorage.deleteFriendship(id, friendId);
            log.info("Пользователь с id {} удален из списка друзей пользователя с id {}", friendId, id);
        }
        if (status == StatusFriendship.UNCONFIRMED) {
            friendshipStorage.deleteFriendship(id, friendId);
            log.info("Пользователь с id {} удален из списка друзей пользователя с id {}", friendId, id);
        }
        if (status == null) {
            log.info("Нельзя удалить несуществующего друга из списка друзей");
        }
    }

    public List<User> getListFriend(long id) {
        return friendshipStorage.getListFriend(id);
    }

    public List<User> getCommonUsers(long id, long friendId) {
        return friendshipStorage.getCommonUsers(id, friendId);
    }

    private void checkExistId(long userId, long friendId)  {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
    }
}
