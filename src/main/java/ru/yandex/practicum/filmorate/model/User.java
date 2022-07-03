package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {
    private Long id;
    @Email(message = "Почта должна быть правильного формата")
    private final String email;
    @NotEmpty(message = "Логин не должен быть пустым")
    private final String login;
    private String name;
    @Past(message = "Дата рождения должна быть в прошлом")
    private final LocalDate birthday;

}
