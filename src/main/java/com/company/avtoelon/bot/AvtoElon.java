package com.company.avtoelon.bot;


import com.company.avtoelon.container.ComponentContainer;
import com.company.avtoelon.controller.AdminController;
import com.company.avtoelon.controller.MainController;
import com.company.avtoelon.service.UserServiceImpl;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class AvtoElon extends TelegramLongPollingBot {
    @Override
    public String getBotToken() {
        return ComponentContainer.BOT_TOKEN;
    }


    @Override
    public String getBotUsername() {
        return ComponentContainer.BOT_USERNAME;
    }

    @Override
    public void onUpdateReceived(Update update) {
        UserServiceImpl userService = new UserServiceImpl();

        if (update.hasMessage()) {

            Message message = update.getMessage();
            User user = message.getFrom();

            com.company.avtoelon.entity.User curUser = userService.getUserByChatId(String.valueOf(message.getChatId()));

            String userRole = userService.getUserRole(String.valueOf(message.getChatId()));

            if (!curUser.getActive()){
                SendMessage sendMessage = new SendMessage(String.valueOf(message.getChatId()),
                        "Siz blocklangansiz!");
                sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            }

            else if (curUser.getFullName() != null && userRole!=null)
                AdminController.handleMessage(user, message);

            else
                MainController.handleMessage(user, message);


        } else if (update.hasCallbackQuery()) {

            CallbackQuery callbackQuery = update.getCallbackQuery();
            Message message = callbackQuery.getMessage();
            User user = callbackQuery.getFrom();
            String data = callbackQuery.getData();

            String userRole = userService.getUserRole(String.valueOf(message.getChatId()));
            if (userService.getUserByChatId(String.valueOf(message.getChatId())).getFullName() != null &&
                    userRole != null)
                AdminController.handleCallback(user, message, data);
            else
                MainController.handleCallback(user, message, data);

        }

    }


    public void sendMsg(Object obj) {
        try {
            if (obj instanceof SendMessage) {
                execute((SendMessage) obj);
            } else if (obj instanceof DeleteMessage) {
                execute((DeleteMessage) obj);
            } else if (obj instanceof EditMessageText) {
                execute((EditMessageText) obj);
            } else if (obj instanceof SendPhoto) {
                execute((SendPhoto) obj);
            } else if (obj instanceof SendDocument) {
                execute((SendDocument) obj);
            } else if (obj instanceof EditMessageReplyMarkup) {
                execute((EditMessageReplyMarkup) obj);
            } else if (obj instanceof SendVideo) {
                execute((SendVideo) obj);
            } else if (obj instanceof SendAudio) {
                execute((SendAudio) obj);
            } else if (obj instanceof SendPoll) {
                execute((SendPoll) obj);
            } else if (obj instanceof SendGame) {
                execute((SendGame) obj);
            } else if (obj instanceof SendVoice) {
                execute((SendVoice) obj);
            } else if (obj instanceof SendAnimation) {
                execute((SendAnimation) obj);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
