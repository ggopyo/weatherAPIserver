package com.example.mongodb.repository;

import com.example.mongodb.model.WeatherItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ItemRepository extends MongoRepository<WeatherItem, String> {

//    @Query("{name:'?0'}")
//    WeatherItem findItemByName(String name);
//
//    @Query(value="{category:'?0'}", fields="{'name' : 1, 'quantity' : 1}")
//    List<WeatherItem> findAll(String category);
//
//


}