package com.company.avtoelon.entity;

import com.company.avtoelon.enums.Region;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Product {
    private Integer id;
    private String name;
    private Double price;
    private String photoId;
    private String createdAt;
    private Boolean active;
    private Integer userId;
    private Integer categoryId;
    private Boolean negotiable;
    private Integer descriptionId;
    private Region region;


    public Product(String name, Double price, String photoId, Integer userId,
                   Integer categoryId, Boolean negotiable, Integer descriptionId, Region region) {
        this.name = name;
        this.price = price;
        this.photoId = photoId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.negotiable = negotiable;
        this.descriptionId = descriptionId;
        this.region = region;
    }

    public Product(String name, Double price, String photoId,
                   Integer userId, Integer categoryId, Boolean negotiable, Region region) {
        this.name = name;
        this.price = price;
        this.photoId = photoId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.negotiable = negotiable;
        this.region = region;
    }
}
