package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

/*
* операции с фильмами, — добавление и удаление лайка,
* вывод 10 наиболее популярных фильмов по количеству лайков. */
@Service
public class FilmService {
    public void addLike(long filmId, long userId){


    }
    public void deleteLike(long filmId, long userId){

    }
    private List<Film> getMostPopularFilms(int count){
        if (count=null){
            //возвращаем первые 10
        }

    }
}
