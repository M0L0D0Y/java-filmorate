package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;

@Component
public class FilmIdGenerator {
    private long id = 0;

    public long generate() {
        return ++id;
    }
}
