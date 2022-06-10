package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * операции с пользователями, как добавление в друзья,
 * удаление из друзей, вывод списка общих друзей*/
@Service
public class UserService {

    private final InMemoryUserStorage memoryUserStorage = InMemoryUserStorage.getInMemoryUserStorage();
    private Set<Long> listFriends = new HashSet<>();

    public void addFriend(long id, long friendId) {
        User user = memoryUserStorage.getUser(id);
        User friend = memoryUserStorage.getUser(friendId);
        listFriends = user.getListFriends();
        listFriends.add(friendId);
        user.setListFriends(listFriends);
        listFriends.clear();
        listFriends = friend.getListFriends();
        friend.setListFriends(listFriends);
        listFriends.clear();
    }

    public void deleteFriend(long id, long friendId) {
        User user = memoryUserStorage.getUser(id);
        User friend = memoryUserStorage.getUser(friendId);
        listFriends = user.getListFriends();
        listFriends.remove(friendId);
        user.setListFriends(listFriends);
        listFriends.clear();
        listFriends = friend.getListFriends();
        listFriends.remove(id);
        friend.setListFriends(listFriends);
        listFriends.clear();
    }

    public List<User> getListFriend(long id) {
        List<User> friends = new ArrayList<>();
        User user = memoryUserStorage.getUser(id);
        listFriends = user.getListFriends();
        for (long value : listFriends) {
            friends.add(memoryUserStorage.getUser(value));
        }
        return friends;
    }
}
