package com.interpreter.relational.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    private String username;
    private String phone;
    private String group;

    public User(String username, String phone, String group) {
        this.username = username;
        this.phone = phone;
        this.group = group;
    }
}
