package com.interpreter.relational.controller;

import com.interpreter.relational.service.InterpreterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RequestMapping("/interpreter")
@RestController
@RequiredArgsConstructor
public class InterpreterController {
    private final InterpreterService interpreterService;

    @GetMapping(value = "/execute")
    public ResponseEntity<Set<Map<String, Collection<String>>>> execute() throws IllegalAccessException {
        List<String> query = Arrays.asList(
                "SELECT R2 WHERE ( NOT phone = 135121 OR username = \"andrew\" ) AND NOT username = \"jim\" -> T1",
                "DIFFERENCE R1 AND T1 -> T2",
                "DIVIDE R3 BY T2 OVER group username -> T3",
                "JOIN T2 AND T3 OVER username -> T4",
                "TIMES R3 AND T4 -> T5",
                "JOIN T5 AND T4 OVER group username phone"
        );

        return ResponseEntity.status(HttpStatus.OK).body(
                interpreterService.resultConversion(
                        interpreterService.inputProcessing(query)
                )
        );
    }
}