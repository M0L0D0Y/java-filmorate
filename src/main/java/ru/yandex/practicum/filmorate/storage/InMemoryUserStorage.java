package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Validator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final InMemoryUserStorage INSTANCE = new InMemoryUserStorage();

    private final Map<Long, User> users = new HashMap<>();
    private final Validator validator = Validator.getValidator();
    private InMemoryUserStorage(){
    }

    @Override
    public Collection<User> getAllUser() {
        return users.values();
    }

    @Override
    public User addUser(User user) throws ValidationException {
        validator.validateUser(user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(long id) {
        users.remove(id);
    }

    @Override
    public User updateUser(User user) throws ValidationException {
        if (user.getId() <= 0) {
            throw new ValidationException("id меньше или равен нулю");
        }
        validator.validateUser(user);
        users.put(user.getId(), user);
        return user;
    }
    public User getUser(long id){
        return users.get(id);
    }
    public static InMemoryUserStorage getInMemoryUserStorage() {
        return INSTANCE;
    }
}
