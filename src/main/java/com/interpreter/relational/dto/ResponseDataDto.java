package com.interpreter.relational.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDataDto {

    Set<Map<String, Collection<String>>> result;

    Map<String, Set<Map<String, Collection<String>>>> getResults;

}
