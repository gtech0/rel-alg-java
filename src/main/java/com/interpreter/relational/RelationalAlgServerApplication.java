package com.interpreter.relational;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@SpringBootApplication
public class RelationalAlgServerApplication {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new GuavaModule())
                .registerModule(new JsonOrgModule())
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    public static void main(String[] args) {
        SpringApplication.run(RelationalAlgServerApplication.class, args);
    }
}
