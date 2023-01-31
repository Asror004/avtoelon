package com.company.avtoelon.controller;

import com.company.avtoelon.container.ComponentContainer;
import com.company.avtoelon.entity.Category;
import com.company.avtoelon.entity.Product;
import com.company.avtoelon.enums.AdminStatus;
import com.company.avtoelon.payload.Result;
import com.company.avtoelon.service.AdminService;
import com.company.avtoelon.service.AdminServiceImpl;
import com.company.avtoelon.service.UserServiceImpl;
import com.company.avtoelon.ui.HandleText;
import com.company.avtoelon.ui.helper.Asror;
import com.company.avtoelon.ui.helper.Otabek;
import com.company.avtoelon.util.keyboardMarkup.InlineKeyboardUtil;
import com.company.avtoelon.util.keyboardMarkup.ReplyKeyboardUtil;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.games.Animation;
import org.telegram.telegrambots.meta.api.objects.games.Game;
import org.telegram.telegrambots.meta.api.objects.payments.Invoice;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.polls.PollOption;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.company.avtoelon.container.ComponentContainer.MY_BOT;
import static com.company.avtoelon.container.ComponentContainer.adminStatus;
import static com.company.avtoelon.util.InlineKeyboardButtonConstants.*;
import static com.company.avtoelon.util.KeyboardButtonConstants.*;


public class AdminController {

    private static AdminServiceImpl adminService = new AdminServiceImpl();
    private static UserServiceImpl userService = new UserServiceImpl();

    public static void handleMessage(User user, Message message) {

        String chatId = String.valueOf(message.getChatId());
        HandleText handleText = new HandleText(adminService,userService,message);
        if (message.hasText()) {
            String text = message.getText();
            handleText(user, message, text);
        }
        if (message.hasPhoto()){

            List<PhotoSize> photo = message.getPhoto();

            SendPhoto sendPhoto = new SendPhoto(String.valueOf(chatId
            ), new InputFile(photo.get(photo.size()-1).getFileId()));

            sendPhoto.setCaption(message.getCaption());

            handleText.sendMessage(sendPhoto,chatId);
        }
        if (message.hasVideo()){

            Video video = message.getVideo();

            SendVideo sendVideo = new SendVideo(String.valueOf(chatId
            ), new InputFile(video.getFileId()));

            sendVideo.setCaption(message.getCaption());

            handleText.sendMessage(sendVideo, chatId);
        }
        if (message.hasAudio()){
            Audio audio = message.getAudio();

            SendAudio sendAudio = new SendAudio(String.valueOf(chatId
            ), new InputFile(audio.getFileId()));

            sendAudio.setCaption(message.getCaption());

            handleText.sendMessage(sendAudio, chatId);
        }
        if (message.hasPoll()){
            Poll poll = message.getPoll();

            List<String> options = new ArrayList<>();
            for (PollOption option : poll.getOptions()) {
                options.add(String.valueOf(option.getText()));
            }

            SendPoll sendPoll = new SendPoll(String.valueOf(chatId
            ), poll.getQuestion(), options);

//
//            sendPoll.setCorrectOptionId(poll.getCorrectOptionId());
//            sendPoll.setCloseDate(poll.getCloseDate());

            handleText.sendMessage(sendPoll, chatId);
        }
        if (message.hasVoice()){

            Voice voice = message.getVoice();

            SendVoice sendVoice = new SendVoice(chatId,
                    new InputFile(voice.getFileId()));

            sendVoice.setCaption(message.getCaption());

            handleText.sendMessage(sendVoice, chatId);
        }
        if (message.hasAnimation()){
            Animation animation = message.getAnimation();

            SendAnimation sendAnimation = new SendAnimation(chatId,
                    new InputFile(animation.getFileId()));

            sendAnimation.setCaption(message.getCaption());

            handleText.sendMessage(sendAnimation, chatId);
        }
    }



    private static void handleText(User user, Message message, String text) {

        message.getText();
        HandleText handleText = new HandleText(new AdminServiceImpl(), new UserServiceImpl(), message);
        String chatId = String.valueOf(message.getChatId());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        switch (text) {

            case "/start" -> {
                sendMessage.setText("Menu");
                sendMessage.setReplyMarkup(ReplyKeyboardUtil.getAdminMenuOne());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            }
            case NEXT_ADMIN_MENU -> {
                sendMessage.setText("Menu");
                sendMessage.setReplyMarkup(ReplyKeyboardUtil.getAdminMenuTwo());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            }
            case BACK_ADMIN_MENU -> {
                sendMessage.setText("Menu");
                sendMessage.setReplyMarkup(ReplyKeyboardUtil.getAdminMenuOne());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                adminStatus.remove(chatId);
            }

            case ADD_CATEGORY -> handleText.addCategory();
            case ALL_CATEGORIES -> handleText.allCategories();
            case CONFIRM_ADS -> handleText.confirmAds();
            case USERS_LIST -> handleText.usersList();
            case BLOCK_USER -> handleText.blockUser();
            case ADD_ADMIN -> handleText.addAdmin();
            case SEND_MESSAGE -> {
                adminStatus.remove(chatId);
                adminStatus.put(chatId, AdminStatus.SEND_MESSAGE);
                sendMessage.setText("Xabarni kiriting");
                sendMessage.setReplyMarkup(ReplyKeyboardUtil.getBackButton());
                MY_BOT.sendMsg(sendMessage);
            }


            default -> {

                if (ComponentContainer.admin_wish.containsKey(chatId)) {
                    Otabek otabek = new Otabek();

                    AdminStatus adminStatus = ComponentContainer.admin_wish.get(chatId);

                    if (adminStatus == AdminStatus.SEND_CATEGORY_NAME) {

                        otabek.sendCategoryName(chatId, text);

                    } else if (adminStatus == AdminStatus.RENAME_CATEGORY) {

                        otabek.renameCategory(text, chatId);


                    } else if (adminStatus == AdminStatus.BLOCK_USER) {

                        otabek.blockUser(text, chatId);

                    } else if (adminStatus == AdminStatus.ADD_ADMIN) {

                        otabek.addAdmin(text, chatId);

                    }
                }

                if (ComponentContainer.adminStatus.containsKey(chatId) && ComponentContainer.adminStatus.get(chatId).
                        equals(AdminStatus.SEND_MESSAGE)) {
                    handleText.sendMessage(text,chatId);
                }

                if (adminStatus.containsKey(chatId) && adminStatus.get(chatId).
                        equals(AdminStatus.EDIT_CATEGORY)) {
                    handleText.editCategory();


                }
            }
        }
    }


    public static void handleCallback(User user, Message message, String data) {

        SendMessage sendMessage = new SendMessage();
        String chatId = String.valueOf(message.getChatId());
        sendMessage.setChatId(chatId);
        Otabek otabek = new Otabek();

        HandleText handleText=new HandleText(adminService,userService,message);

        if (data.startsWith(ADD_CATEGORY_CALLBACK_DEMO)) {

            System.out.println(ComponentContainer.admin_current_category.get(chatId));
            otabek.addCategoryCall(chatId);
            deleteInline(message, chatId);

        } else if (data.startsWith("_category/")) {


            otabek.categoryCall(data, chatId);
            deleteInline(message, chatId);

        }
        else if (data.startsWith(BACK_CATEGORY_CALLBACK_DEMO)) {


            otabek.backCategoryCall(data, chatId);
            deleteInline(message, chatId);

        } else if (data.startsWith(DELETE_CATEGORY_CALLBACK_DEMO)) {

            otabek.deleteCategoryCall(data, chatId);
            deleteInline(message, chatId);


        } else if (data.startsWith(HOME_ADMIN_CALLBACK_DEMO)) {

            sendMessage.setText("Menu");
            sendMessage.setReplyMarkup(ReplyKeyboardUtil.getAdminMenuOne());
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
            deleteInline(message, chatId);

        } else if (data.startsWith(EDIT_CATEGORY_NAME_CALLBACK_DEMO)) {

            ComponentContainer.admin_wish.put(chatId, AdminStatus.RENAME_CATEGORY);
            deleteInline(message, chatId);
            sendMessage.setText("Yangi nom kiriting");
            ComponentContainer.MY_BOT.sendMsg(sendMessage);

        } else if (data.equals("_excel")) {

            File usersEXCEL = adminService.getUsersEXCEL();
            SendDocument sendDocument = new SendDocument(chatId, new InputFile(usersEXCEL));
            ComponentContainer.MY_BOT.sendMsg(sendDocument);
            deleteInline(message,chatId);

        } else if (data.equals("_pdf")) {

            File usersPDF = adminService.getUsersPDF();
            SendDocument sendDocument = new SendDocument(chatId, new InputFile(usersPDF));
            ComponentContainer.MY_BOT.sendMsg(sendDocument);

            deleteInline(message,chatId);
        }


        AdminServiceImpl adminService = new AdminServiceImpl();

        Asror asror = new Asror(message, data, sendMessage, new UserServiceImpl(), new AdminServiceImpl(), new Otabek());

        if (adminStatus.containsKey(chatId) &&
                adminStatus.get(chatId).equals(AdminStatus.SHOW_CATEGORY)) {
            if (data.equals("menu")) {
                sendMessage.setText("Menu");

                adminStatus.remove(chatId);

                DeleteMessage deleteMessage = new DeleteMessage(
                        sendMessage.getChatId(), message.getMessageId());
                MY_BOT.sendMsg(deleteMessage);
                MY_BOT.sendMsg(sendMessage);
                return;
            }

            String[] split = data.split("/");

            if (split[0].equals("back")) {
                asror.back(split[1]);
            }
            if (split[0].equals("delete_category"))
                asror.deleteCategory(split[1]);
            if (split[0].equals("edit_category"))
                asror.editCategory(split[1]);
            else
                asror.showCategories();
        }


        if (adminStatus.containsKey(chatId) && adminStatus.get(chatId)
                .equals(AdminStatus.CONFIRM_PRODUCTS)){

            DeleteMessage deleteMessage = new
                    DeleteMessage(chatId, message.getMessageId());
            MY_BOT.sendMsg(deleteMessage);

            Result result;

            Integer data2;
            if (data.endsWith("r"))
                data2 = Integer.parseInt(data.substring(0, data.length() - 1));
            else
                data2 = Integer.parseInt(data);

            Integer userId = userService.getProduct(data2).getUserId();
            List<com.company.avtoelon.entity.User> users = adminService.getUsers();
            String userChatId = users.stream().filter(user1 ->
                    user1.getId().equals(userId)).findAny().get().getChatId();

            List<PhotoSize> photo = message.getPhoto();
            SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(photo.get(photo.size()-1).getFileId()));
            sendPhoto.setCaption(message.getCaption());

            if (data.endsWith("r")){
                result = adminService.deleteProduct(data2);

                MY_BOT.sendMsg(sendPhoto);

                SendMessage sendMessage2 = new SendMessage(userChatId, "Sizni shu e'loningiz qabul qilinmadi\uD83D\uDC46\uD83D\uDC46\uD83D\uDC46");
                MY_BOT.sendMsg(sendMessage2);
            }else {
                result = adminService.confirmAd(Integer.valueOf(data));

                MY_BOT.sendMsg(sendPhoto);

                SendMessage sendMessage2 = new SendMessage(userChatId, "Sizni shu e'loningiz qabul qilindi\uD83D\uDC46\uD83D\uDC46\uD83D\uDC46");
                MY_BOT.sendMsg(sendMessage2);
            }


            SendMessage sendMessage3 = new SendMessage(chatId, result.getMessage());
            MY_BOT.sendMsg(sendMessage3);

            handleText.confirmAds();
        }


    }


    public static void deleteInline(Message message, String chatId) {
        DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
        if (message.getMessageId() != null)
            ComponentContainer.MY_BOT.sendMsg(deleteMessage);
    }
}