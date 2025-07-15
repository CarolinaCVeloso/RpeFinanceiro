package com.fintech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SistemaPagamentosApplication {

    public static void main(String[] args) {
        SpringApplication.run(SistemaPagamentosApplication.class, args);
    }
} 