package com.company.avtoelon.bot;

import com.company.avtoelon.container.ComponentContainer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {

        try {

            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            AvtoElon myBot = new AvtoElon();
            ComponentContainer.MY_BOT = myBot;

            botsApi.registerBot(myBot);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
