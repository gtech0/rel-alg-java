package com.interpreter.relational.dto;

import com.interpreter.relational.service.RowMap;
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

    Set<RowMap> result;

    Map<String, Set<RowMap>> getResults;

}
