package ru.yandex.practicum.filmorate.service.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendshipMapper implements RowMapper<Friendship> {
    @Override
    public Friendship mapRow(ResultSet rs, int rowNum) throws SQLException {
        Friendship friendship = new Friendship();
        friendship.setUserId(rs.getLong("USER_ID"));
        friendship.setFriendId(rs.getLong("FRIEND_ID"));
        friendship.setStatusId(rs.getInt("STATUS_ID"));
        return friendship;
    }
}