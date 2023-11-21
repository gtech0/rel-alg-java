package com.interpreter.relational;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Multimap;
import com.interpreter.relational.dto.ResponseDataDto;
import com.interpreter.relational.service.InterpreterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@SpringBootTest(classes = RelationalAlgServerApplication.class)
class RelationalAlgServerApplicationTests {

    @Autowired
    private InterpreterService interpreterService;

    @Autowired
    private ObjectMapper mapper;

    private ResponseDataDto testLogic(String[] query) throws IOException {
        String testPath = "classpath:testData.json";
        Map<String, Set<Multimap<String, String>>> data = mapper
                .readValue(ResourceUtils.getFile(testPath), new TypeReference<>() {});

        ResponseDataDto dataDto = interpreterService.inputProcessing(query, data);
        System.out.println(dataDto);
        return dataDto;
    }

    @Test
    void selectTest() throws IOException {
        String[] query = new String[] {
                "SELECT R1 WHERE name = \"andrew\""
        };

        ResponseDataDto dataDto = testLogic(query);
        assert !dataDto.getResult().isEmpty();
    }

    @Test
    void unionTest() throws IOException {
        String[] query = new String[] {
                "UNION R1 AND R2"
        };

        ResponseDataDto dataDto = testLogic(query);
        assert !dataDto.getResult().isEmpty();
    }

    @Test
    void intersectionAndProjectionTest() throws IOException {
        String[] query = new String[] {
                "PROJECT R1 OVER name -> T1",
                "PROJECT R2 OVER name -> T2",
                "INTERSECT T1 AND T2"
        };

        ResponseDataDto dataDto = testLogic(query);
        assert !dataDto.getResult().isEmpty();
    }
}
