package ru.yandex.practicum.filmorate.storage;

import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final Map<Long, User> users = new HashMap<>();
    private final Validator validator;// = Validator.getValidator();

    @Autowired
    private InMemoryUserStorage(Validator validator) {
        this.validator = validator;
    }

    @Override
    public Collection<User> getAllUser() {
        return users.values();
    }

    @Override
    public User addUser(User user) throws ValidationException {
        validator.validateUser(user);
        user.setId(UserIdGenerator.generate());
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

    @Override
    public User getUser(long id) throws NotFoundException {
        Set<Long> listIdUser = users.keySet();
        if (!(listIdUser.contains(id))) {
            throw new NotFoundException("Нет пользователя с таким Id " + id);
        }
        return users.get(id);
    }
}
