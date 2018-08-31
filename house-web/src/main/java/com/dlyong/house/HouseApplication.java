package com.dlyong.house;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
// 开启异步
@EnableAsync
public class HouseApplication {

    public static void main(String[] args) {

        SpringApplication.run(HouseApplication.class);
    }
}
