package com.interpreter.relational;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interpreter.relational.dto.ResponseDataDto;
import com.interpreter.relational.dto.TestDataDto;
import com.interpreter.relational.service.InterpreterService;
import com.interpreter.relational.service.RowMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = RelationalAlgServerApplication.class)
class RelationalAlgServerApplicationTests {

    @Autowired
    private InterpreterService interpreterService;

    @Autowired
    private ObjectMapper mapper;

    private TestDataDto testLogic(List<String> query, String testName) throws IOException, JSONException {
        String dataPath = "classpath:testData.json";
        String resultPath = "classpath:testResult.json";
        Map<String, Set<RowMap>> data = mapper.readValue(ResourceUtils.getFile(dataPath), new TypeReference<>() {});
        Map<String, Set<RowMap>> result = mapper.readValue(ResourceUtils.getFile(resultPath), new TypeReference<>() {});

        ResponseDataDto dataDto = interpreterService.inputProcessing(query, data);

        String json = new JSONArray(result.get(testName).toString()).toString();
        Set<RowMap> expected = mapper.readValue(json, new TypeReference<>() {
        });

        return new TestDataDto(dataDto, expected);
    }

    @Test
    void selectTest() throws IOException, JSONException {
        List<String> query = new ArrayList<>(Arrays.asList(
                "SELECT R1 WHERE R1.name = \"andrew\" AND NOT phone = ( 134122 + 1 / 2 ) AND R1.birthdate > \"1998-01-01\" -> T1",
                "ANSWER T1"
        ));

        TestDataDto testDataDto = testLogic(query, "selectTest");
        ResponseDataDto dataDto = testDataDto.getDataDto();
        Set<RowMap> expected = testDataDto.getExpected();

        assertEquals(expected, dataDto.getResult());
        assert !dataDto.getResult().isEmpty();
    }

    @Test
    void unionTest() throws IOException, JSONException {
        List<String> query = new ArrayList<>(Arrays.asList(
                "UNION R1 AND R2 -> T1",
                "ANSWER T1"
        ));

        TestDataDto testDataDto = testLogic(query, "unionTest");
        ResponseDataDto dataDto = testDataDto.getDataDto();
        Set<RowMap> expected = testDataDto.getExpected();

        assertEquals(expected, dataDto.getResult());
        assert !dataDto.getResult().isEmpty();
    }

    @Test
    void intersectionAndProjectionTest() throws IOException, JSONException {
        List<String> query = new ArrayList<>(Arrays.asList(
                "PROJECT R1 OVER name -> T1",
                "PROJECT R2 OVER name -> T2",
                "INTERSECT T1 AND T2 -> T3",
                "ANSWER T3"
        ));

        TestDataDto testDataDto = testLogic(query, "intersectionAndProjectionTest");
        ResponseDataDto dataDto = testDataDto.getDataDto();
        Set<RowMap> expected = testDataDto.getExpected();

        assertEquals(expected, dataDto.getResult());
        assert !dataDto.getResult().isEmpty();
    }

    @Test
    void differenceAndProjectionTest() throws IOException, JSONException {
        List<String> query = new ArrayList<>(Arrays.asList(
                "PROJECT R1 OVER phone -> T1",
                "PROJECT R2 OVER phone -> T2",
                "DIFFERENCE T1 AND T2 -> T3",
                "ANSWER T3"
        ));

        TestDataDto testDataDto = testLogic(query, "differenceAndProjectionTest");
        ResponseDataDto dataDto = testDataDto.getDataDto();
        Set<RowMap> expected = testDataDto.getExpected();

        assertEquals(expected, dataDto.getResult());
        assert !dataDto.getResult().isEmpty();
    }

    @Test
    void joinTest() throws IOException, JSONException {
        List<String> query = new ArrayList<>(Arrays.asList(
                "JOIN R1 AND R2 OVER name -> T1",
                "ANSWER T1"
        ));

        TestDataDto testDataDto = testLogic(query, "joinTest");
        ResponseDataDto dataDto = testDataDto.getDataDto();
        Set<RowMap> expected = testDataDto.getExpected();

        assertEquals(expected, dataDto.getResult());
        assert !dataDto.getResult().isEmpty();
    }
}
