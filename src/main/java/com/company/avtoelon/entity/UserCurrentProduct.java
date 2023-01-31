package com.company.avtoelon.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class UserCurrentProduct {

    private List<ProductDescription> productDescriptions;
    private int ordinal;


}
