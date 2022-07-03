package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.StatusFriendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class UserService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserStorage memoryUserStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(@Qualifier("InDataBaseUser") UserStorage memoryUserStorage, JdbcTemplate jdbcTemplate) {
        this.memoryUserStorage = memoryUserStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addFriend(long userId, long friendId) throws NotFoundException {
        User user = memoryUserStorage.getUser(userId);//для проверки существования таких ползователей
        User friend = memoryUserStorage.getUser(friendId);

        String query = "SELECT friend_id FROM 'friendship' WHERE user_id = ? AND status_id = ?";
        List<Long> foundIdFriends = jdbcTemplate.query(
                        query,
                        new Object[]{friendId, 1},
                        new BeanPropertyRowMapper<>(Long.class));
        if (foundIdFriends.contains(userId)) {
            String queryForConfirmFriendship1 = "INSERT INTO 'friendship' VALUES(?, ?, ?)";
            jdbcTemplate.update(queryForConfirmFriendship1, userId, friendId, 2);
            String queryForConfirmFriendship2 = "UPDATE 'friendship' SET status_id =? " +
                    "WHERE user_id = ? AND friend_id =?";
            jdbcTemplate.update(queryForConfirmFriendship2, 2, friendId, userId);
            log.info("Дружба между пользователсями с id {} и {} подтверждена", userId, friendId);
        } else {
            String queryForRequestFriendship = "INSERT INTO 'friendship' VALUES(?, ?, ?)";
            jdbcTemplate.update(queryForRequestFriendship, userId, friendId, 1);
            log.info("Пользователь с id {} отправил запрос на дружбу пользователю с id {}", userId, friendId);
        }
    }

    public void deleteFriend(long id, long friendId) throws NotFoundException {
        User user = memoryUserStorage.getUser(id);
        User friend = memoryUserStorage.getUser(friendId);


    }

    public List<User> getListFriend(long id) throws NotFoundException {
        /*String queryForGetIdFriendsById = "SELECT friend_id FROM 'friendship' WHERE user_id = ? AND status_id = ?";
        List<Long> listIdFriend = jdbcTemplate.query(
                queryForGetIdFriendsById,
                new Object[]{id, 2},
                new BeanPropertyRowMapper<>(Long.class));
        String queryForGetAllFriendById = "SELECT * FROM 'users' WHERE user_id = ?";
        List<User> listFriend = new LinkedList<>();
        for (Long userId : listIdFriend) {
            User user = memoryUserStorage.getUser(userId);
            listFriend.add(user);
            log.info("Пользователь с id {} отправил запрос на дружбу пользователю с id {}", userId, friendId);
            return listFriend;
        }*/

        String query = "SELECT * FROM users WHERE user_id IN(SELECT * FROM (SELECT friend_id FROM (" +
                "SELECT friend_id FROM friendship WHERE user_id = ?)))";
        List<User> friends = jdbcTemplate.query(
                query,
                new Object[]{id},
                new BeanPropertyRowMapper<>(User.class));
        log.info("Получили список друзей пользователя с id {}", id);
        return friends;
    }

    public List<User> getCommonUsers(long id, long friendId) throws NotFoundException {
        String query= "SELECT *\n" +
                "FROM users\n" +
                "WHERE user_id IN(\n" +
                "    SELECT *\n" +
                "    FROM (\n" +
                "        SELECT friend_id\n" +
                "        FROM (\n" +
                "            SELECT friend_id\n" +
                "            FROM friendship\n" +
                "            WHERE user_id = ?\n" +
                "            AND status_id IN(\n" +
                "                SELECT status_id\n" +
                "                FROM status_friendship\n" +
                "                WHERE name = \"подтверждена\"))\n" +
                "        WHERE friend_id IN (\n" +
                "            SELECT friend_id\n" +
                "            FROM friendship\n" +
                "            WHERE user_id = ?\n" +
                "            AND status_id IN(\n" +
                "                SELECT status_id\n" +
                "                FROM status_friendship\n" +
                "                WHERE name = \"подтверждена\"))));";
        List<User> commonFriends = jdbcTemplate.query(
                query,
                new Object[]{id, friendId},
                new BeanPropertyRowMapper<>(User.class));
        log.info("Получили список общих друзей пользователей с id {} и {}", id, friendId);
        return commonFriends;
    }
}
