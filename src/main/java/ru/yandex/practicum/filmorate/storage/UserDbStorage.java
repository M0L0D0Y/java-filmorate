package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.Validator;

import java.util.Collection;
import java.util.List;

@Component("InDataBaseUser")
public class UserDbStorage implements UserStorage {

    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);
    private final Validator validator;
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(Validator validator, JdbcTemplate jdbcTemplate) {
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
}
