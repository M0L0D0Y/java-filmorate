package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Validator;
import ru.yandex.practicum.filmorate.service.UserIdGenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Validator validator;
    private final UserIdGenerator userIdGenerator;

    @Autowired
    private InMemoryUserStorage(Validator validator, UserIdGenerator userIdGenerator) {
        this.validator = validator;
        this.userIdGenerator = userIdGenerator;
    }

    @Override
    public Collection<User> getAllUser() {
        return users.values();
    }

    @Override
    public User addUser(User user) {
        validator.validateUser(user);
        user.setId(userIdGenerator.generate());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(long id) {
        Set<Long> listIdUser = users.keySet();
        if (!(listIdUser.contains(id))) {
            throw new NotFoundException("Нет пользователя с таким Id " + id);
        }
        users.remove(id);
    }

    @Override
    public User updateUser(User user) {
        Set<Long> listIdUser = users.keySet();
        if (!(listIdUser.contains(user.getId()))) {
            throw new NotFoundException("Нет пользователя с таким Id " + user.getId());
        }
        validator.validateUser(user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(long id) {
        Set<Long> listIdUser = users.keySet();
        if (!(listIdUser.contains(id))) {
            throw new NotFoundException("Нет пользователя с таким Id " + id);
        }
        return users.get(id);
    }
}
