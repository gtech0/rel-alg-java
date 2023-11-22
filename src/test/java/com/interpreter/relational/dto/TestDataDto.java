package com.interpreter.relational.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestDataDto {

    private ResponseDataDto dataDto;

    private Set<Map<String, Collection<String>>> expected;

}
