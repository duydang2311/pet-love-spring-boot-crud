package com.stc.petlove;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class PetLoveApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetLoveApplication.class, args);
    }

}
