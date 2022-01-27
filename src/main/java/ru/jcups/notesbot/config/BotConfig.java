package ru.jcups.notesbot.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class BotConfig {

    @Value("${telegram.username}")
    private String username;

    @Value("${telegram.token}")
    private String token;

    @Value("${telegram.path}")
    private String path;
}
