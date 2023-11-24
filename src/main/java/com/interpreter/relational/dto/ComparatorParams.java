package com.interpreter.relational.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComparatorParams {

    private String token;

    private String operandLeft;

    private String operandRight;

}
