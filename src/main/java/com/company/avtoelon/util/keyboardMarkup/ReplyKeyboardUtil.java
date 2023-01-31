package com.company.avtoelon.util.keyboardMarkup;

import com.company.avtoelon.util.KeyboardButtonConstants;
import com.company.avtoelon.util.KeyboardButtonConstants.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.company.avtoelon.util.KeyboardButtonConstants.*;

public class ReplyKeyboardUtil {

    private static KeyboardButton getButton(String name) {
        return new KeyboardButton(name);
    }

    private static KeyboardRow getRow(KeyboardButton... buttons) {
        return new KeyboardRow(Arrays.asList(buttons));
    }

    private static ReplyKeyboardMarkup getReply(List<KeyboardRow> keyboardRowList) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboardRowList);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public static ReplyKeyboardMarkup getAdminMenuOne() {
        return getReply(Arrays.asList(getRow(getButton(ADD_CATEGORY), getButton(ALL_CATEGORIES)),
                getRow(getButton(CONFIRM_ADS), getButton(SEND_MESSAGE)),
                getRow(getButton(NEXT_ADMIN_MENU))));

    }

    public static ReplyKeyboardMarkup getAdminMenuTwo() {
        return getReply(Arrays.asList(getRow(getButton(ADD_ADMIN), getButton(BLOCK_USER)),
                getRow(getButton(USERS_LIST)),
                getRow(getButton(BACK_ADMIN_MENU))));

    }

    public static ReplyKeyboard getBackButton() {
        return getReply(Arrays.asList(getRow(getButton(BACK_ADMIN_MENU))));
    }

    public static ReplyKeyboard getUserMenu() {


        return getReply(Arrays.asList(getRow(getButton(ALL_CATEGORIES_USER)),
                getRow(getButton(MY_FAVORITES), getButton(MY_ADS)),
                getRow(getButton(AD), getButton(BALANCE))
        ));


    }


    public static ReplyKeyboard getSendPhoneNumber() {
        KeyboardButton button = getButton("Telefon raqam ulashish");
        button.setRequestContact(true);

        return getReply(Arrays.asList(getRow(button)));
    }
}
