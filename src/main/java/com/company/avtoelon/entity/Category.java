package com.company.avtoelon.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Category {
    private Integer id;
    private String name;
    private Integer parentId;

    public Category(String name, Integer parentId) {
        this.name = name;
        this.parentId = parentId;
    }
}
