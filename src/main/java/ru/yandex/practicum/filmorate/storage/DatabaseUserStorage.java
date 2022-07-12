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
    public void deleteUser(long id) {
        User checkUser = getUser(id);//проверка существования такого id
        String queryDeleteFromUsers = "DELETE FROM FILM_GRADE_USERS WHERE USER_ID = ? AND FILM_ID IN(" +
                "SELECT DISTINCT FILM_ID FROM FILM_GRADE_USERS)";
        jdbcTemplate.update(queryDeleteFromUsers, id);
        String query = "DELETE FROM USERS WHERE USER_ID = ?";
        jdbcTemplate.update(query, id);
        log.info("Пользователь с id = {} удален", id);
    }

    @Override
    public User updateUser(User user) {
        validator.validateUser(user);
        User checkUser = getUser(user.getId());//проверка существования такого id
        String query = "UPDATE USERS SET EMAIL=?, LOGIN=?, NAME=?, BIRTHDAY=?" +
                " WHERE USER_ID=?";
        jdbcTemplate.update(query, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        log.info("Пользователь с id = {} обновлен", user.getId());
        return user;
    }

    @Override
    public User getUser(long id) {
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
}
