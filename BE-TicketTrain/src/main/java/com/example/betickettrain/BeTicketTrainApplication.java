package com.example.betickettrain;

import com.example.betickettrain.service.RedisCacheService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;

@SpringBootApplication
public class BeTicketTrainApplication {
//   @Autowired
//    RedisCacheService redisCacheService;
//   @PostConstruct
//   void updateCache() {
//       redisCacheService.getCachedData("redisCache:train:all");
//   }
    public static void main(String[] args) {
        SpringApplication.run(BeTicketTrainApplication.class, args);
    }

}
