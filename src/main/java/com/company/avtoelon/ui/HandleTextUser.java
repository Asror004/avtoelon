package com.company.avtoelon.ui;

import com.company.avtoelon.container.ComponentContainer;
import com.company.avtoelon.service.AdminServiceImpl;
import com.company.avtoelon.service.UserServiceImpl;
import com.company.avtoelon.util.keyboardMarkup.InlineKeyboardUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

public class HandleTextUser {

    private Message message;
    private User user;
    private String chatId;
    SendMessage sendMessage = new SendMessage();
    AdminServiceImpl adminService=new AdminServiceImpl();
    UserServiceImpl userService=new UserServiceImpl();


    public HandleTextUser(Message message, User user) {
        this.message = message;
        this.user = user;
        chatId = String.valueOf(message.getChatId());
        sendMessage.setChatId(chatId);

    }

    public void allCategoriesUser() {

        sendMessage.setText("Kategoriya tanlang");
        ComponentContainer.categories=userService.getAllCategories();
        System.out.println(ComponentContainer.categories);
        sendMessage.setReplyMarkup(InlineKeyboardUtil.getCategoriesUser(null));
        ComponentContainer.MY_BOT.sendMsg(sendMessage);
        ComponentContainer.user_current_category.put(chatId, null);

    }

    public void ad() {
        sendMessage.setText("Kategoriy tanlang");
        ComponentContainer.currentUserPutAdCategory.put(chatId,null);
        sendMessage.setReplyMarkup(InlineKeyboardUtil.getUserAdCategories(null));
        sendMessage.setChatId(chatId);
        ComponentContainer.MY_BOT.sendMsg(sendMessage);
    }

    public void balance() {
        // Fayzullo
    }
}
