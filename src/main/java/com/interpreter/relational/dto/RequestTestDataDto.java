package com.interpreter.relational.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestTestDataDto {

    private String[] query;

    private Map<String, Set<Map<String, String>>> relations;

}
