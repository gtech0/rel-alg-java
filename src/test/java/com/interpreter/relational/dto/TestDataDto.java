package com.interpreter.relational.dto;

import com.interpreter.relational.service.RowMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestDataDto {

    private ResponseDataDto dataDto;

    private Set<RowMap> expected;

}
