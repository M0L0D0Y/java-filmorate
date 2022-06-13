package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;

@Component
public class UserIdGenerator {
    private long id = 0;

    public long generate() {
        return ++id;
    }
}

