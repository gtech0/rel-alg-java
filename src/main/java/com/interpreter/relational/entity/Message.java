package com.interpreter.relational.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Message {
    private String username;
    private String text;

    public Message(String username, String text) {
        this.username = username;
        this.text = text;
    }
}
