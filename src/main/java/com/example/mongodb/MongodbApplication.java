package com.example.mongodb;


import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.example.mongodb.model.WeatherItem;
import com.example.mongodb.repository.ItemRepository;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableMongoRepositories
@EnableScheduling
public class MongodbApplication {
    //y8YlOsRl3U%2BsizzI%2F8XAFyZ%2BEC2%2BpC%2BZKvmaydgd9gcLtLjRon2iL9FUHQkrvbOOKn%2F%2FI1AYuT41c1b9FWK8aw%3D%3D
    public static void main(String[] args) {
        SpringApplication.run(MongodbApplication.class, args);
    }
}