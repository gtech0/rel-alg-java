package com.interpreter.relational;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RelationalAlgServerApplication {

    @Bean
    public Module guavaModule() {
        return new GuavaModule();
    }

    @Bean
    public Module jsonOrgModule() {
        return new JsonOrgModule();
    }

    public static void main(String[] args) {
        SpringApplication.run(RelationalAlgServerApplication.class, args);
    }
}
