package com.interpreter.relational.controller;

import com.interpreter.relational.dto.ResultDto;
import com.interpreter.relational.service.InterpreterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InterpreterController.class)
public class InterpreterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InterpreterService service;

    @Test
    void inputResponseShouldBeCorrect() throws Exception {
        String[] testQuery = new String[] {
                "SELECT R2 WHERE ( NOT phone = 135121 OR username = \"andrew\" ) AND NOT username = \"jim\" -> T1",
                "DIFFERENCE R1 AND T1 -> T2",
                "DIVIDE R3 BY T2 OVER username -> T3",
                "JOIN T2 AND T3 OVER username -> T4",
                "TIMES R3 AND T4 -> T5",
                "JOIN T4 AND T5 OVER group username phone"
        };

        when(service.validation(testQuery, "sol1")).thenReturn(new ResultDto("OK"));
        this.mockMvc
                .perform(post("/interpreter/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "query": [
                                        "SELECT R2 WHERE ( NOT phone = 135121 OR username = \\"andrew\\" ) AND NOT username = \\"jim\\" -> T1",
                                        "DIFFERENCE R1 AND T1 -> T2",
                                        "DIVIDE R3 BY T2 OVER username -> T3",
                                        "JOIN T2 AND T3 OVER username -> T4",
                                        "TIMES R3 AND T4 -> T5",
                                        "JOIN T4 AND T5 OVER group username phone"
                                    ],
                                    "problemName": "sol1"
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(content().json("""
                            {
                                "result": "OK"
                            }
                        """)
                );
    }

}
