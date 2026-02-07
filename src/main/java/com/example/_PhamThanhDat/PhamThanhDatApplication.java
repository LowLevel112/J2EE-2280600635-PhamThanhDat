package com.example._PhamThanhDat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PhamThanhDatApplication {

    public static void main(String[] args) {
        System.setProperty("spring.config.name", "phamthanhdatapplication");
        SpringApplication.run(PhamThanhDatApplication.class, args);
    }

}
