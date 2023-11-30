package com.interpreter.relational.dto;

import com.google.common.collect.Multimap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestTestDataDto {

    private List<String> query;

    private Map<String, Set<Multimap<String, String>>> relations;

}
