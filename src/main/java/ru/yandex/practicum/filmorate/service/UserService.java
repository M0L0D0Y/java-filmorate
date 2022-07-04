package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.mappers.FriendshipMapper;
import ru.yandex.practicum.filmorate.service.mappers.UserMapper;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class UserService {
    private static final int NO_STATUS = 0;
    private static final int UNCONFIRMED = 1;
    private static final int CONFIRMED = 2;
    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserStorage memoryUserStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(@Qualifier("InDataBaseUser") UserStorage memoryUserStorage, JdbcTemplate jdbcTemplate) {
        this.memoryUserStorage = memoryUserStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addFriend(long userId, long friendId) throws NotFoundException {
        if (userId == friendId) {
            log.info("Нельзя отправить запрос на дружбу самому себе. Одинаковые id {}={}", userId, friendId);
            return;
        }
        checkExistId(userId, friendId);
        int statusIdUserFriend = getStatusIdUserFriend(userId, friendId);
        if (statusIdUserFriend == UNCONFIRMED) {
            log.info("Пользователь с id {} уже отправлял запрос на дружбу пользователю с id {}", userId, friendId);
        }
        if (statusIdUserFriend == CONFIRMED) {
            log.info("Пользователь с id {} уже дружит с пользователем с id {}", userId, friendId);
        }
        if (statusIdUserFriend == NO_STATUS) {
            log.info("Такой запрос делается впервые. Проверим на возможность подтвердить дружбу");
            int statusIdFriendUser = getStatusIdFriendUser(userId, friendId);
            if (statusIdFriendUser == UNCONFIRMED) {
                confirmFriendship(userId, friendId);
                log.info("Пользователь с id {} подтвердил запрос дружбы пользователя с id {}", userId, friendId);
            }
            if (statusIdFriendUser == NO_STATUS) {
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
        int statusId = getStatusIdUserFriend(id, friendId);
        if (statusId == CONFIRMED) {
            changeStatusFriendshipFriend(id, friendId);
            log.info("Статус дружбы у бывшего друга обновлен");
            deleteFriendshipUser(id, friendId);
            log.info("Пользователь с id {} удален из списка друзей пользователя с id {}", friendId, id);
        }
        if (statusId == UNCONFIRMED) {
            deleteFriendshipUser(id, friendId);
            log.info("Пользователь с id {} удален из списка друзей пользователя с id {}", friendId, id);
        }
        if (statusId == NO_STATUS) {
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

    private int getStatusIdUserFriend(long userId, long friendId) {
        String queryFriendshipCheck = "SELECT * FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
        Friendship friendship = jdbcTemplate.query(
                        queryFriendshipCheck,
                        new FriendshipMapper(),
                        userId,
                        friendId)
                .stream()
                .findAny()
                .orElse(new Friendship());
        return friendship.getStatusId();
    }

    private int getStatusIdFriendUser(long userId, long friendId) {
        String queryFriendshipCheckReverse = "SELECT * FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
        Friendship friendshipCheck = jdbcTemplate.query(
                        queryFriendshipCheckReverse,
                        new FriendshipMapper(),
                        friendId,
                        userId)
                .stream()
                .findAny()
                .orElse(new Friendship());
        return friendshipCheck.getStatusId();
    }

    private void confirmFriendship(long userId, long friendId) {
        String queryConfirmFriendshipFriend = "UPDATE  FRIENDSHIP SET STATUS_ID = ? " +
                "WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(queryConfirmFriendshipFriend, 2, friendId, userId);
        String queryConfirmFriendshipUser = "INSERT INTO FRIENDSHIP VALUES(?, ?, ?)";
        jdbcTemplate.update(queryConfirmFriendshipUser, userId, friendId, 2);
    }

    private void sendRequestFriendship(long userId, long friendId) {
        String queryRequestFriendship = "INSERT INTO FRIENDSHIP VALUES(?, ?, ?)";
        jdbcTemplate.update(queryRequestFriendship, userId, friendId, 1);
    }

    private void deleteFriendshipUser(long userId, long friendId) {
        String queryDeleteRequestFriendshipUser = "DELETE FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(queryDeleteRequestFriendshipUser, userId, friendId);
    }

    private void changeStatusFriendshipFriend(long userId, long friendId) {
        String queryChangeStatusFriendshipFriend = "UPDATE  FRIENDSHIP SET STATUS_ID = ? " +
                "WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(queryChangeStatusFriendshipFriend, 1, friendId, userId);
    }
}
