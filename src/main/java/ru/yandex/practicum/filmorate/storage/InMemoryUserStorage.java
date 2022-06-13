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
import java.util.Set;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final InMemoryUserStorage INSTANCE = new InMemoryUserStorage();

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
        Set<Long> listIdUser = users.keySet();
        if (!(listIdUser.contains(id))) {
            throw new NotFoundException("Нет пользователя с таким Id " + id);
        }
        users.remove(id);
    }

    @Override
    public User updateUser(User user) throws ValidationException, NotFoundException {
        Set<Long> listIdUser = users.keySet();
        if (!(listIdUser.contains(user.getId()))) {
            throw new NotFoundException("Нет пользователя с таким Id " + user.getId());
        }
        validator.validateUser(user);
        users.put(user.getId(), user);
        return user;
    }

    public User getUser(long id) throws NotFoundException {
        Set<Long> listIdUser = users.keySet();
        if (!(listIdUser.contains(id))) {
            throw new NotFoundException("Нет пользователя с таким Id " + id);
        }
        return users.get(id);
    }

    public static InMemoryUserStorage getInMemoryUserStorage() {
        return INSTANCE;
    }
}
