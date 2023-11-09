package com.interpreter.relational.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataDto {

    private List<String> query;

    private Map<String, Set<Map<String, String>>> relations;

}
