package com.company.avtoelon.controller;

import com.company.avtoelon.container.ComponentContainer;
import com.company.avtoelon.container.ComponentContainer.*;
import com.company.avtoelon.entity.*;
import com.company.avtoelon.enums.Region;
import com.company.avtoelon.enums.UserStatus;
import com.company.avtoelon.payload.Result;
import com.company.avtoelon.service.AdminServiceImpl;
import com.company.avtoelon.service.UserService;
import com.company.avtoelon.service.UserServiceImpl;
import com.company.avtoelon.ui.HandleTextUser;
import com.company.avtoelon.ui.helper.Asror;
import com.company.avtoelon.ui.helper.Otabek;
import com.company.avtoelon.util.keyboardMarkup.InlineKeyboardUtil;
import com.company.avtoelon.util.keyboardMarkup.ReplyKeyboardUtil;
import javassist.runtime.Desc;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.*;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;


import java.util.ArrayList;
import java.util.List;

import static com.company.avtoelon.container.ComponentContainer.*;
import static com.company.avtoelon.enums.UserStatus.*;
import static com.company.avtoelon.util.InlineKeyboardButtonConstants.*;
import static com.company.avtoelon.util.KeyboardButtonConstants.*;


public class MainController {

    static UserServiceImpl userService = new UserServiceImpl();
    static AdminServiceImpl adminService = new AdminServiceImpl();


    public static void handleMessage(User user, Message message) {

        String chatId = String.valueOf(message.getChatId());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        if (message.hasText()) {
            String text = message.getText();
            handleText(user, message, text);
        } else if (message.hasPhoto()) {

            PhotoSize photoSize = message.getPhoto().get(message.getPhoto().size() - 1);

            ProductDescription productDescription = userProductDescription.get(message.getChatId().toString());
            productDescription.setPhoto_id(photoSize.getFileId());

            com.company.avtoelon.entity.User userByChatId = userService.getUserByChatId(String.valueOf(message.getChatId()));
            productDescription.setUser_id(userByChatId.getId());
            userPutAdStatus.put(chatId, UserStatus.CONFIRM_PRODUCT);

            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId);
            sendPhoto.setPhoto(new InputFile(productDescription.getPhoto_id()));
            sendPhoto.setReplyMarkup(InlineKeyboardUtil.confirmAdUser());
            Otabek otabek = new Otabek();
            sendPhoto.setCaption(otabek.getCaption2(productDescription));
            MY_BOT.sendMsg(sendPhoto);


        } else if (message.hasContact()) {
            handleContact(user, message, message.getContact());
        }
    }

    private static void handleContact(User user, Message message, Contact contact) {
        com.company.avtoelon.entity.User newUser = new com.company.avtoelon.entity.User(
                contact.getFirstName(), contact.getPhoneNumber(),
                String.valueOf(message.getChatId()));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));

        UserService userService = new UserServiceImpl();
        Result result = userService.addUser(newUser);
        if (result.isSuccess()) {
            sendMessage.setText("Menu");
            sendMessage.setReplyMarkup(ReplyKeyboardUtil.getUserMenu());
        } else {
            sendMessage.setText(result.getMessage());
            sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        }
        MY_BOT.sendMsg(sendMessage);
    }

    private static void handleText(User user, Message message, String text) {

        HandleTextUser handleTextUser = new HandleTextUser(message, user);

        String chatId = String.valueOf(message.getChatId());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        Asror asror = new Asror(message, "", sendMessage, userService, adminService, new Otabek());


        switch (text) {
            case "/start" -> {

                if (userService.getUserByChatId(chatId).getFullName() != null) {
                    sendMessage.setText("Menu");
                    sendMessage.setReplyMarkup(ReplyKeyboardUtil.getUserMenu());
                } else {
                    sendMessage.setText("Telefon raqam ulashing");
                    sendMessage.setReplyMarkup(ReplyKeyboardUtil.getSendPhoneNumber());
                }
                MY_BOT.sendMsg(sendMessage);
            }
            case ALL_CATEGORIES_USER -> handleTextUser.allCategoriesUser();
//            case FILTER -> handleTextUser.filterCategories();
            case MY_ADS -> {
                userStatus.put(chatId, UserStatus.SHOW_MY_PRODUCTS);
                order.put(chatId, 0);
                asror.showMyAds();
            }
            case MY_FAVORITES -> {
                userStatus.put(chatId, UserStatus.SHOW_FAVORITES);
                order.put(chatId, 0);
                asror.showFavorites();
            }
            case AD -> handleTextUser.ad();
            case BALANCE -> handleTextUser.balance();


            default -> {

                if (userProductDescription.get(chatId) != null) {

                    UserStatus userStatus = userPutAdStatus.get(chatId);

                    ProductDescription productDescription = userProductDescription.get(chatId);

                    if (userStatus == UserStatus.SEND_AD_NAME) {

                        productDescription.setName(text);

                        userPutAdStatus.put(chatId, UserStatus.SEND_PRODUCT_PRICE);
                        sendMessage.setText("\uD83D\uDCB5 Narx ni kiriting");
                        MY_BOT.sendMsg(sendMessage);

                    } else if (userStatus == UserStatus.SEND_PRODUCT_PRICE) {

                        try {
                            double price = Double.parseDouble(text);
                            productDescription.setPrice(price);
                            userPutAdStatus.put(chatId, UserStatus.IS_NEGOTIABLE);

                            sendMessage.setText("Kami bormi?");
                            sendMessage.setReplyMarkup(InlineKeyboardUtil.getIsNegotiable());

                        } catch (NumberFormatException ex) {
                            sendMessage.setText("Raqam formati noto'gri. Qaytadan kiriting");
                            ex.printStackTrace();
                        }

                        MY_BOT.sendMsg(sendMessage);

                    } else if (userStatus == UserStatus.SEND_PRODUCT_INFO) {

                        productDescription.setInfo_(text);
                        userPutAdStatus.put(chatId, SEND_CONTACT_INFO);
                        sendMessage.setText("Bog'lanish uchun ma'lumot qoldiring");
                        MY_BOT.sendMsg(sendMessage);
                    } else if (userStatus == SEND_CONTACT_INFO) {

                        productDescription.setPhone_number(text);
                        userPutAdStatus.put(chatId, SEND_PHOTO);
                        sendMessage.setText("Mahsulot uchun rasm yuboring");
                        MY_BOT.sendMsg(sendMessage);

                    }
                }

            }
        }

    }

    public static void handleCallback(User user, Message message, String data) {

        AdminServiceImpl adminService = new AdminServiceImpl();
        UserServiceImpl userService = new UserServiceImpl();

        SendMessage sendMessage = new SendMessage();
        String chatId = String.valueOf(message.getChatId());
        sendMessage.setChatId(chatId);
        HandleTextUser handleTextUser = new HandleTextUser(message, user);

        Otabek otabek = new Otabek();


        if (data.startsWith("_user_category/")) {

            otabek.changeUserCurrentCategory(data, message, user);
            deleteInline(message, chatId);

        } else if (data.startsWith(BACK_USER_CALLBACK)) {

            otabek.backUserCategory(data, message, chatId);
            deleteInline(message, chatId);

        } else if (data.startsWith(SEARCH_CALLBACK)) {

            System.out.println("ComponentContainer.user_current_search_regions = " + user_current_search_regions);
            List<String> list = new ArrayList<>();
            list.add(Region.ALL.toString());

            user_current_search_regions.put(chatId, list);
            sendMessage.setText("Viloyatni tanlang");
            sendMessage.setReplyMarkup(InlineKeyboardUtil.getRegions(Region.ALL, chatId));
            MY_BOT.sendMsg(sendMessage);
            deleteInline(message, chatId);

        } else if (data.startsWith("_region/")) {

            Region region = Region.valueOf(data.split("/")[1]);

            EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
            editMessageReplyMarkup.setMessageId(message.getMessageId());
            editMessageReplyMarkup.setReplyMarkup(InlineKeyboardUtil.getRegions(region, chatId));
            editMessageReplyMarkup.setChatId(chatId);
            MY_BOT.sendMsg(editMessageReplyMarkup);


        } else if (data.startsWith(SEARCH_REGION_CALL_BACK)) {

            userCurrentProductPriceInterval.put(chatId, "0-100000");
            otabek.getPriceInterval(chatId);
            deleteInline(message, chatId);

        } else if (data.equals(HOME_USER_CALLBACK)) {

            otabek.homeUser(chatId);
            deleteInline(message, chatId);

        } else if (data.equals(NEXT_AD_CALLBACK)) {


            otabek.nextAd(chatId);
            deleteInline(message, chatId);

        } else if (data.equals(PREV_AD_CALLBACK)) {

            otabek.prevAd(chatId);
            deleteInline(message, chatId);

        } else if (data.equals(ADD_FAVORITE_CALLBACK)) {

            otabek.addFavorite(chatId);

        } else if (data.equals(CONTINUE_SEARCHING_CALLBACK)) {

            handleTextUser.allCategoriesUser();
            deleteInline(message, chatId);

        } else if (data.startsWith("_number/")) {

            otabek.addNumber(chatId, message, data);

        } else if (data.startsWith(ERASE_LAST_SIGN_CALLBACK)) {

            otabek.eraseLastSign(chatId);
            otabek.eraseLastSignExecute(message, chatId);

        } else if (data.startsWith(SEARCH_PRICE_CALLBACK)) {

            if (otabek.validatePriceInterval(chatId))
                deleteInline(message, chatId);

        } else if (data.equals(MINUS_CALLBACK)) {
            otabek.putMinus(chatId);
            otabek.minusExecute(message, chatId);
        } else if (data.startsWith(BACK_USER_AD_CALLBACK)) {


            if (!data.split("/")[1].equals("null")) {

                Integer categoryId = Integer.valueOf(data.split("/")[1]);

                Category categoryById = adminService.getCategoryById(categoryId);

                Category parent = adminService.getCategoryById(categoryById.getParentId());


                if (parent != null) {
                    sendMessage.setText(parent.getName());
                    sendMessage.setReplyMarkup(InlineKeyboardUtil.getUserAdCategories(parent.getId()));
                } else {
                    sendMessage.setText("Kategoriya tanlang");
                    sendMessage.setReplyMarkup(InlineKeyboardUtil.getUserAdCategories(null));
                }


            } else {
                sendMessage.setText("Menu");
                sendMessage.setReplyMarkup(ReplyKeyboardUtil.getUserMenu());
            }
            MY_BOT.sendMsg(sendMessage);
            deleteInline(message, chatId);


        } else if (data.startsWith("_put_ad")) {


            Integer id = Integer.valueOf(data.split("/")[1]);

            currentUserPutAdCategory.put(chatId, id);
            ProductDescription productDescription = new ProductDescription();
            productDescription.setCategory_id(id);
            userProductDescription.put(chatId, productDescription);

            sendMessage.setText("Nom kiriting");
            userPutAdStatus.put(chatId, UserStatus.SEND_AD_NAME);
            MY_BOT.sendMsg(sendMessage);

            deleteInline(message, chatId);


        } else if (data.startsWith(PUT_AD_CALLBACK)) {

            Integer id = Integer.valueOf(data.split("/")[1]);
            Category category = userService.getCategory(id);
            sendMessage.setText(category.getName());
            sendMessage.setReplyMarkup(InlineKeyboardUtil.getUserAdCategories(id));
            MY_BOT.sendMsg(sendMessage);
            deleteInline(message, chatId);

        } else if (data.equals("_negotiable")) {


            ProductDescription productDescription = userProductDescription.get(chatId);
            productDescription.setNegotiable(true);

            userPutAdStatus.put(chatId, UserStatus.SEND_LOCATION);
            sendMessage.setText("Hududni tanlang");
            sendMessage.setReplyMarkup(InlineKeyboardUtil.getAdLocation());
            MY_BOT.sendMsg(sendMessage);

            deleteInline(message, chatId);


        } else if (data.equals("_not_negotiable")) {

            ProductDescription productDescription = userProductDescription.get(chatId);
            productDescription.setNegotiable(false);

            userPutAdStatus.put(chatId, UserStatus.SEND_LOCATION);
            sendMessage.setText("Hududni tanlang");
            sendMessage.setReplyMarkup(InlineKeyboardUtil.getAdLocation());
            MY_BOT.sendMsg(sendMessage);

            deleteInline(message, chatId);


        } else if (data.startsWith("ad_location/")) {
            String region = data.split("/")[1];

            ProductDescription productDescription = userProductDescription.get(chatId);
            productDescription.setRegion(region);

            userPutAdStatus.put(chatId, UserStatus.SEND_PRODUCT_INFO);
            sendMessage.setText("Mahsulot haqida qo'shimcha ma'lumotlarini kiriting");
            MY_BOT.sendMsg(sendMessage);

            deleteInline(message, chatId);
        } else if (data.equals("_confirm_ad_user")) {


            if (userPutAdStatus.get(chatId) == CONFIRM_PRODUCT) {
                ProductDescription productDescription = userProductDescription.get(chatId);

                Product product = new Product();
                product.setActive(false);
                product.setCategoryId(productDescription.getCategory_id());
                product.setPrice(productDescription.getPrice());
                product.setRegion(Region.valueOf(productDescription.getRegion()));
                product.setNegotiable(productDescription.isNegotiable());
                product.setPhotoId(productDescription.getPhoto_id());
                product.setUserId(productDescription.getUser_id());
                product.setName(productDescription.getName());

                Description description = new Description();
                description.setText(productDescription.getInfo_());
                description.setPhoneNumber(productDescription.getPhone_number());


                Result result = userService.addProduct(product, description);

                sendMessage.setText(result.getMessage());
                MY_BOT.sendMsg(sendMessage);

            }
            deleteInline(message, chatId);
        } else if (data.equals("_cancel_ad_user")) {

            cleanCache(chatId);

        }


        Asror asror = new Asror(message, data, sendMessage, userService, adminService, new Otabek());
        if (userStatus.containsKey(chatId) &&
                userStatus.get(chatId).equals(UserStatus.SHOW_FAVORITES) ||
                userStatus.get(chatId).equals(UserStatus.SHOW_MY_PRODUCTS)) {
            if (data.startsWith("r")) {


                String[] split = data.split("/");
                if (!userStatus.get(chatId).equals(UserStatus.SHOW_MY_PRODUCTS)) {

                    Result result = userService.removeFavorite(Integer.parseInt(split[1]),
                            Integer.parseInt(split[2]));

                    SendMessage sendMessage2 = new SendMessage(chatId, result.getMessage());
                    MY_BOT.sendMsg(sendMessage2);

                    Integer userId = userService.getUserByChatId(chatId).getId();
                    if (userService.getFavorites(userId).size() == order.get(chatId)) {
                        order.put(chatId, order.get(chatId) - 1);
                    }

                    asror.showFavorites();
                    deleteInline(message, chatId);
                } else {
                    Result result = adminService.deleteProduct(Integer.valueOf(split[1]));

                    Integer userId = userService.getUserByChatId(chatId).getId();
                    if (userService.getMyProducts(userId).size() == order.get(chatId)) {
                        order.put(chatId, order.get(chatId) - 1);
                    }
                    asror.showMyAds();

                    SendMessage sendMessage2 = new SendMessage(chatId, result.getMessage());
                    MY_BOT.sendMsg(sendMessage2);
                }
            } else if (data.equals("back")) {
                if (!userStatus.get(chatId).equals(UserStatus.SHOW_MY_PRODUCTS)) {
                    order.put(chatId, order.get(chatId) - 1);
                    asror.showFavorites();
                } else {
                    order.put(chatId, order.get(chatId) - 1);
                    asror.showMyAds();
                }
                deleteInline(message, chatId);
            } else if (data.equals("next")) {
                if (!userStatus.get(chatId).equals(UserStatus.SHOW_MY_PRODUCTS)) {
                    order.put(chatId, order.get(chatId) + 1);
                    asror.showFavorites();
                } else {
                    order.put(chatId, order.get(chatId) + 1);
                    asror.showMyAds();
                }
                deleteInline(message, chatId);
            } else if (data.equals("main")) {
                order.remove(chatId);
                sendMessage.setText("Menyu");
                sendMessage.setReplyMarkup(ReplyKeyboardUtil.getUserMenu());
                deleteInline(message, chatId);
                MY_BOT.sendMsg(sendMessage);
            }
        }


    }


    public static void deleteInline(Message message, String chatId) {
        DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
        if (message.getMessageId() != null)
            MY_BOT.sendMsg(deleteMessage);
    }

    public static void cleanCache(String chatId) {
        userProductDescription.remove(chatId);
        userPutAdStatus.remove(chatId);
        userPutAdStatus.remove(chatId);
        userStatus.remove(chatId);
        userCurrentProduct.remove(chatId);
        user_current_category.remove(chatId);
    }
}