package com.interpreter.relational;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RelationalAlgServerApplication {

    @Bean
    public Module guavaModule() {
        return new GuavaModule();
    }

    public static void main(String[] args) {
        SpringApplication.run(RelationalAlgServerApplication.class, args);
    }
}
