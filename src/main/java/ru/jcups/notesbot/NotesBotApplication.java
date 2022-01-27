package ru.jcups.notesbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class NotesBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotesBotApplication.class, args);
    }
}
