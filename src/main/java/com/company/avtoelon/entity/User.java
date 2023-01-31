package com.company.avtoelon.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User {

    private Integer id;
    private String fullName;
    private String phoneNumber;
    private Boolean active = true;
    private Double balance;
    private String chatId;


    public User(String fullName, String phoneNumber,
                String chatId) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.active = active;
        this.balance = balance;
        this.chatId = chatId;
    }
}
