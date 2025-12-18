package com.gravity.ourmoments;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.gravity.ourmoments.mapper")
public class OurMomentsApplication {

    public static void main(String[] args) {
        SpringApplication.run(OurMomentsApplication.class, args);
    }

}
