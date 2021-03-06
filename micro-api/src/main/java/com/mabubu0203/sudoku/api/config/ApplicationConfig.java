package com.mabubu0203.sudoku.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component(value = "com.mabubu0203.sudoku.api.config.ApplicationConfig")
public class ApplicationConfig {

    @Value("${system.name:not-found}")
    @Getter
    @Setter
    private String systemName;

}
