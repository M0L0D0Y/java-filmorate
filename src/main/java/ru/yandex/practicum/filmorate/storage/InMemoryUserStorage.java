package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Validator;
import ru.yandex.practicum.filmorate.service.UserIdGenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final UserStorage INSTANCE = new InMemoryUserStorage();

    private final Map<Long, User> users = new HashMap<>();
    private final Validator validator = Validator.getValidator();

    private InMemoryUserStorage() {
    }

    @Override
    public Collection<User> getAllUser() {
        return users.values();
    }

    @Override
    public User addUser(User user) throws ValidationException {
        user.setId(UserIdGenerator.generate());
        validator.validateUser(user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(long id) throws NotFoundException {
        if (id <= 0) {
            throw new NotFoundException("Id должен быть больше нуля " + id);
        }
        if (!(users.containsKey(id))) {
            throw new NotFoundException("Пользователя с таким id нет " + id);
        }
        users.remove(id);
    }

    @Override
    public User updateUser(User user) throws ValidationException, NotFoundException {
        if (user.getId() <= 0) {
            throw new NotFoundException("id меньше или равен нулю " + user.getId());
        }
        validator.validateUser(user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(long id) throws NotFoundException {
        if (id <= 0) {
            throw new NotFoundException("Id должен быть больше нуля " + id);
        }
        if (!(users.containsKey(id))) {
            throw new NotFoundException("Пользователя с таким id нет " + id);
        }
        return users.get(id);
    }

    public static UserStorage getInMemoryUserStorage() {
        return INSTANCE;
    }
}
