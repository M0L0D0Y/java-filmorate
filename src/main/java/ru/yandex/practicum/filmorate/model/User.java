package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {
    private long id;
    @Email(message = "Почта должна быть правильного формата")
    private String email;
    @NotEmpty(message = "Логин не должен быть пустым")
    private String login;
    private String name;
    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthday;
}
