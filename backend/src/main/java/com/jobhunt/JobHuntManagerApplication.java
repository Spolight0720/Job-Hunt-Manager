package com.jobhunt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.jobhunt.mapper")
public class JobHuntManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobHuntManagerApplication.class, args);
    }
}
