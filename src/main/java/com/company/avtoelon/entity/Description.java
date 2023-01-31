package com.company.avtoelon.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Description {
    private Integer id; // Long
    private String text;
    private String phoneNumber;
    private Long NumberOfView;

    public Description(String text, String phoneNumber) {
        this.text = text;
        this.phoneNumber = phoneNumber;
    }
}
