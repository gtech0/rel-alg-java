package com.interpreter.relational.dto;

import com.google.common.collect.Multimap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDataDto {

    Set<Multimap<String, String>> result;

    Map<String, Set<Multimap<String, String>>> getResults;

}
