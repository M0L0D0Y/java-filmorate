package ru.yandex.practicum.filmorate.service.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Grades;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GradesMapper implements RowMapper<Grades> {
    @Override
    public Grades mapRow(ResultSet rs, int rowNum) throws SQLException {
        Grades grades = new Grades();
        grades.setGrade(rs.getDouble(1));
        return grades;
    }
}
