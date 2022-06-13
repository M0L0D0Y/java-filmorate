package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

/*
 * операции с пользователями, как добавление в друзья,
 * удаление из друзей, вывод списка общих друзей*/
@Service
public class UserService {

    private final UserStorage memoryUserStorage;
    @Autowired
    public UserService(InMemoryUserStorage memoryUserStorage) {
        this.memoryUserStorage = memoryUserStorage;

    }

    public void addFriend(long id, long friendId) throws NotFoundException {
        User user = memoryUserStorage.getUser(id);
        User friend = memoryUserStorage.getUser(friendId);
        Set<Long> listFriends;
        listFriends = user.getListFriends();
        listFriends.add(friendId);
        user.setListFriends(listFriends);
        listFriends.clear();
        listFriends = friend.getListFriends();
        friend.setListFriends(listFriends);
        listFriends.clear();
    }

    public void deleteFriend(long id, long friendId) throws NotFoundException {
        User user = memoryUserStorage.getUser(id);
        User friend = memoryUserStorage.getUser(friendId);
        Set<Long> listFriends;
        listFriends = user.getListFriends();
        listFriends.remove(friendId);
        user.setListFriends(listFriends);
        listFriends.clear();
        listFriends = friend.getListFriends();
        listFriends.remove(id);
        friend.setListFriends(listFriends);
        listFriends.clear();
    }

    public List<User> getListFriend(long id) throws NotFoundException {
        List<User> friends = new ArrayList<>();
        User user = memoryUserStorage.getUser(id);
        Set<Long> listFriends;
        listFriends = user.getListFriends();
        for (long value : listFriends) {
            friends.add(memoryUserStorage.getUser(value));
        }
        return friends;
    }
    public List<User> getCommonUsers(long id, long friendId) throws NotFoundException {
        List<Long> userListFriend = new LinkedList<>(memoryUserStorage.getUser(id).getListFriends());
        List<Long> friendListFriend = new LinkedList<>(memoryUserStorage.getUser(friendId).getListFriends());
        List<User> commonFriends  = new LinkedList<>();
        userListFriend.retainAll(friendListFriend);
        for (long value : userListFriend) {
            commonFriends.add(memoryUserStorage.getUser(value));
        }
        return commonFriends;
    }
}
