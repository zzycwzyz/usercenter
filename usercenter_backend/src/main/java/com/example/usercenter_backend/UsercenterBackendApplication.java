package com.example.usercenter_backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.example.usercenter_backend.mapper")
@SpringBootApplication
public class UsercenterBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsercenterBackendApplication.class, args);
    }

}
