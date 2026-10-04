package com.vansh.resume_screening.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@RestController
public class KeepAliveController {

    private final DataSource dataSource;

    public KeepAliveController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/api/keep-alive")
    public String keepAlive() {

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT 1");
                ResultSet resultSet = statement.executeQuery()
        ) {

            if (resultSet.next() && resultSet.getInt(1) == 1) {
                return "OK - Render and MySQL are alive";
            }

            return "OK - MySQL responded";

        } catch (Exception e) {
            throw new RuntimeException("Database keep-alive failed", e);
        }
    }
}