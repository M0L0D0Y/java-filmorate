package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.StatusFriendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.mappers.FriendshipMapper;
import ru.yandex.practicum.filmorate.service.mappers.UserMapper;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserStorage memoryUserStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(@Qualifier("DatabaseUserStorage") UserStorage memoryUserStorage, JdbcTemplate jdbcTemplate) {
        this.memoryUserStorage = memoryUserStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addFriend(long userId, long friendId) throws NotFoundException {
        if (userId == friendId) {
            log.info("Нельзя отправить запрос на дружбу самому себе. Одинаковые id {}={}", userId, friendId);
            return;
        }
        checkExistId(userId, friendId);
        StatusFriendship statusUserFriend = getStatusUserFriend(userId, friendId);
        if (statusUserFriend == StatusFriendship.UNCONFIRMED) {
            log.info("Пользователь с id {} уже отправлял запрос на дружбу пользователю с id {}", userId, friendId);
        }
        if (statusUserFriend == StatusFriendship.CONFIRMED) {
            log.info("Пользователь с id {} уже дружит с пользователем с id {}", userId, friendId);
        }
        if (statusUserFriend == null) {
            log.info("Такой запрос делается впервые. Проверим на возможность подтвердить дружбу");
            StatusFriendship statusIdFriendUser = getStatusFriendUser(userId, friendId);
            if (statusIdFriendUser == StatusFriendship.UNCONFIRMED) {
                confirmFriendship(userId, friendId);
                log.info("Пользователь с id {} подтвердил запрос дружбы пользователя с id {}", userId, friendId);
            }
            if (statusIdFriendUser == null) {
                sendRequestFriendship(userId, friendId);
                log.info("Пользователь с id {} отправил запрос на дружбу пользователю с id {}", userId, friendId);
            }
        }
    }

    public void deleteFriend(long id, long friendId) throws NotFoundException {
        if (id == friendId) {
            log.info("Нельзя удалить себя из друзей. Одинаковые id {}={}", id, friendId);
            return;
        }
        checkExistId(id, friendId);
        StatusFriendship status = getStatusUserFriend(id, friendId);
        if (status == StatusFriendship.CONFIRMED) {
            changeStatusFriendshipFriend(id, friendId);
            log.info("Статус дружбы у бывшего друга обновлен");
            deleteFriendshipUser(id, friendId);
            log.info("Пользователь с id {} удален из списка друзей пользователя с id {}", friendId, id);
        }
        if (status == StatusFriendship.UNCONFIRMED) {
            deleteFriendshipUser(id, friendId);
            log.info("Пользователь с id {} удален из списка друзей пользователя с id {}", friendId, id);
        }
        if (status == null) {
            log.info("Нельзя удалить несуществующего друга из списка друзей");
        }
    }

    public List<User> getListFriend(long id) throws NotFoundException {
        String query = "SELECT * FROM USERS WHERE USER_ID IN (SELECT FRIEND_ID FROM FRIENDSHIP WHERE USER_ID = ?)";
        List<User> friends = jdbcTemplate.query(
                query,
                new UserMapper(),
                id);
        log.info("Получили список друзей пользователя с id {}", id);
        return friends;
    }

    public List<User> getCommonUsers(long id, long friendId) throws NotFoundException {
        String query = "SELECT *FROM USERS WHERE USER_ID IN(" +
                "SELECT *FROM (" +
                "SELECT FRIEND_ID FROM (" +
                "SELECT FRIEND_ID FROM FRIENDSHIP WHERE USER_ID = ?) " +
                "WHERE FRIEND_ID IN (" +
                "SELECT FRIEND_ID FROM FRIENDSHIP WHERE USER_ID = ?)))";
        List<User> commonFriends = jdbcTemplate.query(
                query,
                new UserMapper(),
                id,
                friendId);
        log.info("Получили список общих друзей пользователей с id {} и {}", id, friendId);
        return commonFriends;
    }

    private void checkExistId(long userId, long friendId) throws NotFoundException {
        User user = memoryUserStorage.getUser(userId);
        User friend = memoryUserStorage.getUser(friendId);
    }

    private StatusFriendship getStatusUserFriend(long userId, long friendId) {
        String queryFriendshipCheck = "SELECT * FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
        Friendship friendship = jdbcTemplate.query(
                        queryFriendshipCheck,
                        new FriendshipMapper(),
                        userId,
                        friendId)
                .stream()
                .findAny()
                .orElse(new Friendship());
        return friendship.getStatus();
    }

    private StatusFriendship getStatusFriendUser(long userId, long friendId) {
        String queryFriendshipCheckReverse = "SELECT * FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
        Friendship friendshipCheck = jdbcTemplate.query(
                        queryFriendshipCheckReverse,
                        new FriendshipMapper(),
                        friendId,
                        userId)
                .stream()
                .findAny()
                .orElse(new Friendship());
        return friendshipCheck.getStatus();
    }

    private void confirmFriendship(long userId, long friendId) {
        String queryConfirmFriendshipFriend = "UPDATE  FRIENDSHIP SET STATUS_ID = ? " +
                "WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(queryConfirmFriendshipFriend, StatusFriendship.CONFIRMED, friendId, userId);
        String queryConfirmFriendshipUser = "INSERT INTO FRIENDSHIP VALUES(?, ?, ?)";
        jdbcTemplate.update(queryConfirmFriendshipUser, userId, friendId, StatusFriendship.CONFIRMED);
    }

    private void sendRequestFriendship(long userId, long friendId) {
        String queryRequestFriendship = "INSERT INTO FRIENDSHIP VALUES(?, ?, ?)";
        jdbcTemplate.update(queryRequestFriendship, userId, friendId, StatusFriendship.UNCONFIRMED.toString());
    }

    private void deleteFriendshipUser(long userId, long friendId) {
        String queryDeleteRequestFriendshipUser = "DELETE FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(queryDeleteRequestFriendshipUser, userId, friendId);
    }

    private void changeStatusFriendshipFriend(long userId, long friendId) {
        String queryChangeStatusFriendshipFriend = "UPDATE  FRIENDSHIP SET STATUS_ID = ? " +
                "WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(queryChangeStatusFriendshipFriend, StatusFriendship.UNCONFIRMED, friendId, userId);
    }
}
