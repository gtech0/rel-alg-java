package com.interpreter.relational.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private Date timestamp;

    private int status;

    private String message;

    private String type;

}
