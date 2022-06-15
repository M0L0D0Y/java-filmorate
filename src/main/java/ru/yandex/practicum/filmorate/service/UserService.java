package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

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
        Set<Long> listFriendsUser = user.getFriendsList();
        listFriendsUser.add(friendId);
        user.setFriendsList(listFriendsUser);
        Set<Long> listFriendsFriend = friend.getFriendsList();
        listFriendsFriend.add(id);
        friend.setFriendsList(listFriendsFriend);
    }

    public void deleteFriend(long id, long friendId) throws NotFoundException {
        User user = memoryUserStorage.getUser(id);
        User friend = memoryUserStorage.getUser(friendId);
        Set<Long> listFriendsUser = user.getFriendsList();
        listFriendsUser.remove(friendId);
        user.setFriendsList(listFriendsUser);
        Set<Long> listFriendsFriend = friend.getFriendsList();
        listFriendsFriend.remove(id);
        friend.setFriendsList(listFriendsFriend);
    }

    public List<User> getListFriend(long id) throws NotFoundException {
        List<User> friends = new ArrayList<>();
        User user = memoryUserStorage.getUser(id);
        Set<Long> listIdFriends = user.getFriendsList();
        if (listIdFriends.isEmpty()) {
            return friends;
        }
        for (long value : listIdFriends) {
            friends.add(memoryUserStorage.getUser(value));
        }
        return friends;
    }

    public List<User> getCommonUsers(long id, long friendId) throws NotFoundException {
        List<Long> userListFriend = new LinkedList<>(memoryUserStorage.getUser(id).getFriendsList());
        List<Long> friendListFriend = new LinkedList<>(memoryUserStorage.getUser(friendId).getFriendsList());
        List<User> commonFriends = new LinkedList<>();
        userListFriend.retainAll(friendListFriend);
        for (long value : userListFriend) {
            commonFriends.add(memoryUserStorage.getUser(value));
        }
        return commonFriends;
    }
}
