package com.company.avtoelon.test;

import com.company.avtoelon.entity.Category;
import com.company.avtoelon.entity.Product;
import com.company.avtoelon.entity.User;
import com.company.avtoelon.payload.Result;
import com.company.avtoelon.service.AdminServiceImpl;
import com.company.avtoelon.service.UserServiceImpl;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        AdminServiceImpl adminService = new AdminServiceImpl();
        UserServiceImpl userService = new UserServiceImpl();

//        Result result = adminService.deleteCategory(3);
//        System.out.println(result.getMessage());
//        System.out.println(result.isSuccess());

//        Result result = adminService.deleteProduct(2);
//        System.out.println(result.getMessage());
//        System.out.println(result.isSuccess());

//        Result result = adminService.blockUser(1);
//        System.out.println(result.getMessage());
//        System.out.println(result.isSuccess());

        List<Category> categories = userService.getCategories(null);
        categories.forEach(System.out::println);

//        List<Product> products = userService.getProductsList(4);
//        products.forEach(System.out::println);

//        List<Product> products = userService.getFavorites(1);
//        products.forEach(System.out::println);

//        List<User> users = adminService.getUsers();
//        users.forEach(System.out::println);

//        adminService.getUsersPDF();

//        adminService.getUsersEXCEL();

//        Result jek = userService.addUser(new User
//                ("Asrorxo'ja", "+998909000500", true, 0d, "7555"));
//        System.out.println(jek.getMessage());
//        System.out.println(jek.isSuccess());

//        Result result = adminService.addAdmin("+998909000000");
//        System.out.println(result);

//        Result result = adminService.confirmAd(5);
//        System.out.println(result);

//        Result res = adminService.addCategory(new Category("Kamaz", 11));
//        System.out.println(res);

//        Result result = adminService.editCategoryName(14, "");
//        System.out.println(result);
//
//        User user = userService.getUserByChatId("5556");
//        System.out.println(user);

//        Result result = userService.addUser(new User
//                ("AAA", "+998905555556",
//                        true, 0d, "5556"));
//        System.out.println(result);

//        Result result = userService.setBalance("5556", -12000d);
//        System.out.println(result);
    }
}
