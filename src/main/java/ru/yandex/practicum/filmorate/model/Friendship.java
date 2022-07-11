package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
public class Friendship {
    private long userId;
    private long friendId;
    @Enumerated(EnumType.STRING)
    private StatusFriendship status;
}
