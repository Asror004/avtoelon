package com.company.avtoelon.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Favorite {
    private Integer id;
    private Integer productId;
    private Integer userId; // Long
}
