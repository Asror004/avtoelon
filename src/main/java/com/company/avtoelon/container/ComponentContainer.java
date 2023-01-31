package com.company.avtoelon.container;

import com.company.avtoelon.bot.AvtoElon;
import com.company.avtoelon.entity.*;
import com.company.avtoelon.enums.AdminStatus;
import com.company.avtoelon.enums.UserStatus;


import java.util.*;

public class ComponentContainer {

    public static AvtoElon MY_BOT = null;
    public static String BOT_USERNAME = "https://t.me/Avtoadbot";
    public static String BOT_TOKEN = "5768886364:AAHgFneNvbrocM9lYYUiyA46g61X0XfrLuY";
//
//    public static String BOT_TOKEN = "5554002985:AAG2Hj9P9AzaFJGUIozIX9_05qMUp7_3Ljk";
//    public static String BOT_USERNAME = "t.me/avto_elonuz_bot";

    public static List<Category> categories = new ArrayList<>();
    public static List<User>users=new ArrayList<>();


    public static HashMap<String, AdminStatus> admin_wish = new HashMap<>();
    public static HashMap<String, Integer> admin_current_category = new HashMap<>();

    public static HashMap<String, Integer> user_current_category = new HashMap<>();
    public static HashMap<String,List<String>>user_current_search_regions=new HashMap<>();
    public static List<ProductDescription>productDescriptions=new ArrayList<>();
    public static HashMap<String, UserCurrentProduct>userCurrentProduct=new HashMap<>();

    public static HashMap<String, String> userCurrentProductPriceInterval =new HashMap<>();
    public static HashMap<String, UserStatus> userPutAdStatus=new HashMap<>();
    public static HashMap<String,ProductDescription>userProductDescription=new HashMap<>();
    public static HashMap<String,Integer>currentUserPutAdCategory=new HashMap<>();



    public static HashMap<String, AdminStatus> adminStatus = new HashMap<>();
    public static Map<String, Integer> categoryId = new HashMap<>();
    public static Map<String, List<Product>> confirmStatus = new HashMap<>();



    public static Map<String,UserStatus> userStatus=new HashMap<>();
    public static Map<String, Integer> order = new HashMap<>();
}
