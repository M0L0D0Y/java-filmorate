package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.Validator;
import ru.yandex.practicum.filmorate.service.mappers.FriendshipMapper;
import ru.yandex.practicum.filmorate.service.mappers.UserMapper;

import java.util.Collection;
import java.util.List;

@Slf4j
@Component("DatabaseUserStorage")
public class DatabaseUserStorage implements UserStorage {

    private final Validator validator;
    private final JdbcTemplate jdbcTemplate;

    public DatabaseUserStorage(Validator validator, JdbcTemplate jdbcTemplate) {
        this.validator = validator;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> getAllUser() {
        String query = "SELECT * FROM USERS";
        List<User> userList = jdbcTemplate.query(
                query,
                new UserMapper());
        log.info("Все пользователи получены");
        return userList;
    }

    @Override
    public User addUser(User user) throws ValidationException {
        validator.validateUser(user);
        String sqlQuery = "INSERT INTO USERS(EMAIL, LOGIN, NAME, BIRTHDAY) " +
                "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday());
        String queryForReturnUser = "SELECT * FROM USERS WHERE EMAIL = ?";
        return jdbcTemplate.query(queryForReturnUser,
                        new UserMapper(),
                        user.getEmail())
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Ошибка вставки. " +
                        "Пользователь с email " + user.getEmail() + " не найден."));
    }

    @Override
    public void deleteUser(long id) throws NotFoundException {
        User checkUser = getUser(id);//проверка существования такого id
        String query = "DELETE  FROM USERS WHERE USER_ID = ?";
        jdbcTemplate.update(query, id);
        log.info("Пользователь с id = {} удален", id);
    }

    @Override
    public User updateUser(User user) throws NotFoundException, ValidationException {
        validator.validateUser(user);
        User checkUser = getUser(user.getId());//проверка существования такого id
        String query = "UPDATE USERS SET EMAIL=?, LOGIN=?, NAME=?, BIRTHDAY=?" +
                " WHERE USER_ID=?";
        jdbcTemplate.update(query, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        log.info("Пользователь с id = {} обновлен", user.getId());
        return user;
    }

    @Override
    public User getUser(long id) throws NotFoundException {
        String query = "SELECT * FROM USERS WHERE USER_ID = ?";
        User user = jdbcTemplate.query(
                        query,
                        new UserMapper(),
                        id)
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Пользователь с идентификатором " + id + " не найден."));
        log.info("Пользователь с идентификатором {} найден.", id);
        return user;
    }

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
        return friendship.getStatus();
    }
    public void updateStatusFriendship(long userId, long friendId, FriendshipStatus value) {
        String queryConfirmFriendshipFriend = "UPDATE  FRIENDSHIP SET STATUS_ID = ? " +
                "WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(queryConfirmFriendshipFriend, FriendshipStatus.CONFIRMED, friendId, userId);
        String queryConfirmFriendshipUser = "INSERT INTO FRIENDSHIP VALUES(?, ?, ?)";
        jdbcTemplate.update(queryConfirmFriendshipUser, userId, friendId, FriendshipStatus.CONFIRMED);
    }
    public void addFriendship(long userId, long friendId) {
        String queryRequestFriendship = "INSERT INTO FRIENDSHIP VALUES(?, ?, ?)";
        jdbcTemplate.update(queryRequestFriendship, userId, friendId, FriendshipStatus.UNCONFIRMED.toString());
    }
    public void deleteFriendship(long userId, long friendId) {
        String queryDeleteRequestFriendshipUser = "DELETE FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(queryDeleteRequestFriendshipUser, userId, friendId);
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
}
