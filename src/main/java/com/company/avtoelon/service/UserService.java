package com.company.avtoelon.service;

import com.company.avtoelon.entity.Category;
import com.company.avtoelon.entity.Description;
import com.company.avtoelon.entity.Product;
import com.company.avtoelon.entity.User;
import com.company.avtoelon.payload.Result;
import javassist.runtime.Desc;

import java.util.List;

public interface UserService {


    Result addUser(User user); // Mirsaid

    Result addProduct(Product product, Description description); // Mirsaid

    Result addFavorite(Integer productId, Integer userId); // Mirsaid

    Result removeFavorite(Integer productId, Integer userId); // Mirsaid

    List<Category> getCategories(Integer parentId);// ok

    List<Product> getProductsList(Integer categoryId, String rol);// ok

    List<Product> getFavorites(Integer userId);// ok

    public Product getProduct(Integer productId);

    User getUserByChatId(String chatId);


    String getUserRole(String chatId);
}


