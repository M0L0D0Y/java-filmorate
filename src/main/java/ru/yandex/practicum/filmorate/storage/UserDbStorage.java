package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Validator;
import ru.yandex.practicum.filmorate.service.UserIdGenerator;

import java.util.Collection;

@Component("InDataBaseUser")
public class UserDbStorage implements UserStorage {

    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);
    private final Validator validator;
    private final UserIdGenerator userIdGenerator;
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(Validator validator, UserIdGenerator userIdGenerator, JdbcTemplate jdbcTemplate) {
        this.validator = validator;
        this.userIdGenerator = userIdGenerator;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> getAllUser() {
        String query = "SELECT * FROM 'users'";
        log.info("Все пользователи получены");
        return jdbcTemplate.query(
                query,
                new BeanPropertyRowMapper<>(User.class));

    }

    @Override
    public User addUser(User user) throws ValidationException {
        validator.validateUser(user);
        user.setId(userIdGenerator.generate());
        String query = "INSERT INTO 'users' VALUES(?, ?, ?, ?, ?)";
        jdbcTemplate.update(query, user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        log.info("Пользователь с id = {} добавлен", user.getId());
        return user;
    }

    @Override
    public void deleteUser(long id) throws NotFoundException {
        String query = "DELETE  FROM 'users' WHERE 'user_id' = ?";
        jdbcTemplate.update(query, id);
        log.info("Пользователь с id = {} удален", id);
    }

    @Override
    public User updateUser(User user) throws NotFoundException, ValidationException {
        validator.validateUser(user);
        String query = "UPDATE 'users' SET user_email=?, user_login=?,user_name=?, user_birthday=? WHERE user_id=?";
        jdbcTemplate.update(query, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        log.info("Пользователь с id = {} обновлен", user.getId());
        return user;
    }

    @Override
    public User getUser(long id) throws NotFoundException {
        String query = "SELECT * FROM 'users' WHERE 'user_id' = ?";
        log.info("Пользователь с идентификатором {} найден.", id);
        return jdbcTemplate.query(
                        query,
                        new Object[]{id},
                        new BeanPropertyRowMapper<>(User.class))
                .stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("Пользователь с идентификатором " + id + " не найден."));
    }
}
