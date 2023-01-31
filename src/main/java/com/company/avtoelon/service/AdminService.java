package com.company.avtoelon.service;

import com.company.avtoelon.entity.Category;
import com.company.avtoelon.entity.User;
import com.company.avtoelon.payload.Result;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

import java.io.File;
import java.util.List;

public interface AdminService {

    List<User> getUsers();// ok

    File getUsersPDF();// ok

    File getUsersEXCEL();// ok
    Result addAdmin(String phoneNumber); // FAYZULLO //ok

    Result
    sendMessage(String message, String chatId);
    Result sendMessage(SendPhoto sendPhoto, String chatId);// FAYZULLO //ok

    Result confirmAd(Integer productId);// FAYZULLO //ok

    Result addCategory(Category category); // FAYZULLO // ok

    Result editCategoryName(Integer categoryId, String newName); // FAYZULLO // ok

    Result deleteCategory(Integer categoryId);// ok

    Result deleteProduct(Integer product_id);// ok

    Result blockUser(Integer userId);// ok

    Category getCategoryById(Integer id);







}
