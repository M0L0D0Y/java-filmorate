package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.mappers.FriendshipMapper;
import ru.yandex.practicum.filmorate.service.mappers.UserMapper;

import java.util.List;

@Slf4j
@Component("DatabaseFriendshipStorage")
public class DatabaseFriendshipStorage implements FriendshipStorage {
    private final JdbcTemplate jdbcTemplate;

    public DatabaseFriendshipStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public FriendshipStatus getStatusFriendship(long userId, long friendId) {
        String queryFriendshipCheck = "SELECT * FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
        Friendship friendship = jdbcTemplate.query(
                        queryFriendshipCheck,
                        new FriendshipMapper(),
                        userId,
                        friendId)
                .stream()
                .findAny()
                .orElse(new Friendship());
        log.info("Получили статус дружбы между пользователсями с id {} и {}", userId, friendId);
        return friendship.getStatus();
    }

    @Override
    public void updateStatusFriendship(long userId, long friendId, FriendshipStatus value) {
        String queryConfirmFriendshipFriend = "UPDATE  FRIENDSHIP SET STATUS_ID = ? " +
                "WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(queryConfirmFriendshipFriend, value, friendId, userId);
        String queryConfirmFriendshipUser = "INSERT INTO FRIENDSHIP VALUES(?, ?, ?)";
        jdbcTemplate.update(queryConfirmFriendshipUser, userId, friendId, value);
        log.info("Обновили статус дружбы между пользователсями с id {} и {} на {}", userId, friendId, value.toString());
    }

    @Override
    public void addFriendship(long userId, long friendId) {
        String queryRequestFriendship = "INSERT INTO FRIENDSHIP VALUES(?, ?, ?)";
        jdbcTemplate.update(queryRequestFriendship, userId, friendId, FriendshipStatus.UNCONFIRMED.toString());
        log.info("Пользователь с id {} отправил запрос на дружбу пользователю с id {}", userId, friendId);
    }

    @Override
    public void deleteFriendship(long userId, long friendId) {
        String queryDeleteRequestFriendshipUser = "DELETE FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(queryDeleteRequestFriendshipUser, userId, friendId);
        log.info("Пользователь с id {} удалил из друзей пользователя с id {}", userId, friendId);
    }

    @Override
    public List<User> getListFriend(long id) {
        String query = "SELECT * FROM USERS WHERE USER_ID IN (SELECT FRIEND_ID FROM FRIENDSHIP WHERE USER_ID = ?)";
        List<User> friends = jdbcTemplate.query(
                query,
                new UserMapper(),
                id);
        log.info("Получили список друзей пользователя с id {}", id);
        return friends;
    }

    @Override
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
}
