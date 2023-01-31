package com.company.avtoelon.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductDescription {

 private int id;
 private String name;
 private double price;
 private String photo_id;
 private Date created_at;
 private boolean active;
 private Integer user_id;
 private int category_id;
 private boolean negotiable;
 private Integer description_id;
 private String region;
 private Integer d_id;
 private String info_;
 private String phone_number;
 private long views;

}
