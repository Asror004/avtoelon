package com.company.avtoelon.ui;

import com.company.avtoelon.container.ComponentContainer;
import com.company.avtoelon.entity.Category;
import com.company.avtoelon.entity.Description;
import com.company.avtoelon.entity.Product;
import com.company.avtoelon.enums.AdminStatus;
import com.company.avtoelon.payload.Result;
import com.company.avtoelon.service.AdminServiceImpl;
import com.company.avtoelon.service.UserServiceImpl;
import com.company.avtoelon.ui.helper.Asror;
import com.company.avtoelon.ui.helper.Otabek;
import com.company.avtoelon.util.keyboardMarkup.InlineKeyboardUtil;
import com.company.avtoelon.util.keyboardMarkup.ReplyKeyboardUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.company.avtoelon.container.ComponentContainer.*;
import static com.company.avtoelon.enums.AdminStatus.*;

@AllArgsConstructor
@Data
public class HandleText {

    AdminServiceImpl adminService;
    UserServiceImpl userService;

    SendMessage sendMessage = new SendMessage();


    private Message message;
    private String chatId;

    public HandleText(AdminServiceImpl adminService, UserServiceImpl userService, Message message) {
        this.adminService = adminService;
        this.userService = userService;
        this.message = message;
        chatId = String.valueOf(message.getChatId());
        sendMessage.setChatId(chatId);
    }


    public void addCategory() {

        ComponentContainer.categories = userService.getAllCategories();
        ComponentContainer.admin_current_category.put(chatId, null);
        sendMessage.setText("Kategoriya tanlang");
        sendMessage.setReplyMarkup(InlineKeyboardUtil.getCategories1(null));
        MY_BOT.sendMsg(sendMessage);

    }

    public void allCategories() {
        adminStatus.put(chatId, AdminStatus.SHOW_CATEGORY);
        categoryId.put(chatId, null);

        List<Category> categories = userService.getCategories(null);
        if (categories.isEmpty()) {
            sendMessage.setText("Kategoriyalar mavjud emas");
        } else {
            sendMessage.setText("Kategoriyalar");
            sendMessage.setReplyMarkup(InlineKeyboardUtil.getCategories(categories));
        }

        MY_BOT.sendMsg(sendMessage);
    }


    public void confirmAds() {
        List<Product> products = userService.getProductsList(null, "admin");

        if (products.isEmpty()) {
            sendMessage.setText("Tasdiqlanishi kerak bo'lgan maxsulotlar mavjud emas");
            MY_BOT.sendMsg(sendMessage);
        }

        confirmStatus.put(chatId, products);

        adminStatus.put(chatId, AdminStatus.CONFIRM_PRODUCTS);

        Product product = products.get(0);
        products.remove(product);

        SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(product.getPhotoId()));

        Asror asror = new Asror(message, "data", sendMessage, userService, adminService, new Otabek());

        Description description = userService.getDescription(product.getId());

        String productText = asror.getCaption(product, description);
        sendPhoto.setReplyMarkup(InlineKeyboardUtil.getConfirmAndRemove(product.getId()));
        sendPhoto.setCaption(productText);
        MY_BOT.sendMsg(sendPhoto);
    }

    public void usersList() {
        sendMessage.setText("Tanlang");
        sendMessage.setReplyMarkup(
                InlineKeyboardUtil.getExportUserList());
        MY_BOT.sendMsg(sendMessage);
    }

    public void blockUser() {

        ComponentContainer.admin_wish.put(chatId, BLOCK_USER);
        sendMessage.setText("Telefon raqam kiriting (+998999318685 | 993451234)");
        MY_BOT.sendMsg(sendMessage);


    }

    public void addAdmin() {
        ComponentContainer.admin_wish.put(chatId, ADD_ADMIN);
        sendMessage.setText("Telefon raqam kiriting (+998999318685 | 993451234)");
        MY_BOT.sendMsg(sendMessage);
    }

    public void sendMessage(String text, String userChatId) {
        Result result = adminService.sendMessage(text,userChatId);
        SendMessage sendMessage = new SendMessage(chatId, result.getMessage());

        if (result.isSuccess()) {
            sendMessage.setReplyMarkup(ReplyKeyboardUtil.getAdminMenuTwo());
        }

        MY_BOT.sendMsg(sendMessage);
    }

    public void sendMessage(SendPhoto sendPhoto,String chatId) {

        Result result = adminService.sendMessage(sendPhoto,chatId);

        sendMessage.setText(result.getMessage());

        if (result.isSuccess()) {
            sendMessage.setReplyMarkup(ReplyKeyboardUtil.getAdminMenuTwo());
        }
        MY_BOT.sendMsg(sendMessage);
    }

    public void sendMessage(SendVideo sendVideo, String chatId) {

        Result result = adminService.sendMessage(sendVideo, chatId);

        sendMessage.setText(result.getMessage());

        if (result.isSuccess()) {
            sendMessage.setReplyMarkup(ReplyKeyboardUtil.getAdminMenuTwo());
        }
        MY_BOT.sendMsg(sendMessage);
    }

    public void sendMessage(SendAudio sendAudio, String chatId) {

        Result result = adminService.sendMessage(sendAudio, chatId);

        sendMessage.setText(result.getMessage());

        if (result.isSuccess()) {
            sendMessage.setReplyMarkup(ReplyKeyboardUtil.getAdminMenuTwo());
        }
        MY_BOT.sendMsg(sendMessage);
    }

    public void sendMessage(SendPoll sendPoll, String chatId) {

        Result result = adminService.sendMessage(sendPoll, chatId);

        sendMessage.setText(result.getMessage());

        if (result.isSuccess()) {
            sendMessage.setReplyMarkup(ReplyKeyboardUtil.getAdminMenuTwo());
        }
        MY_BOT.sendMsg(sendMessage);
    }
    public void sendMessage(SendVoice sendVoice, String chatId) {

        Result result = adminService.sendMessage(sendVoice, chatId);

        sendMessage.setText(result.getMessage());

        if (result.isSuccess()) {
            sendMessage.setReplyMarkup(ReplyKeyboardUtil.getAdminMenuTwo());
        }
        MY_BOT.sendMsg(sendMessage);
    }
    public void sendMessage(SendAnimation sendAnimation, String chatId) {

        Result result = adminService.sendMessage(sendAnimation, chatId);

        sendMessage.setText(result.getMessage());

        if (result.isSuccess()) {
            sendMessage.setReplyMarkup(ReplyKeyboardUtil.getAdminMenuTwo());
        }
        MY_BOT.sendMsg(sendMessage);
    }

    public void editCategory() {
        String chatId = sendMessage.getChatId();

        Result result = adminService.editCategoryName(categoryId.
                get(chatId), message.getText());

        if (result.isSuccess()) {
            categoryId.remove(chatId);
            adminStatus.remove(chatId);

            sendMessage.setReplyMarkup(ReplyKeyboardUtil.getAdminMenuOne());
        }
        sendMessage.setText(result.getMessage());

        MY_BOT.sendMsg(sendMessage);
    }
}
