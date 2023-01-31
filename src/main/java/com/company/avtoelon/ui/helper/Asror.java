package com.company.avtoelon.ui.helper;

import com.company.avtoelon.entity.Category;
import com.company.avtoelon.entity.Description;
import com.company.avtoelon.entity.Product;
import com.company.avtoelon.entity.User;
import com.company.avtoelon.enums.AdminStatus;
import com.company.avtoelon.service.AdminService;
import com.company.avtoelon.service.AdminServiceImpl;
import com.company.avtoelon.service.UserServiceImpl;
import com.company.avtoelon.util.keyboardMarkup.InlineKeyboardUtil;
import com.company.avtoelon.util.keyboardMarkup.ReplyKeyboardUtil;
import lombok.AllArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.company.avtoelon.container.ComponentContainer.*;
import static com.company.avtoelon.container.ComponentContainer.MY_BOT;


@AllArgsConstructor
public class Asror {

    Message message;
    String data;
    SendMessage sendMessage;
    UserServiceImpl userService;
    AdminService adminService;
     Otabek otabek;



    public void showCategories() {
        List<Category> categories = userService.getCategories(Integer.valueOf(data));

        Category category = userService.getCategory(Integer.valueOf(data));

        if (categories.isEmpty()) {
            sendMessage.setText(category.getName() + "da kategoriyalar mavjud emas");
            sendMessage.setReplyMarkup(InlineKeyboardUtil.getDelete(category));
        } else {
            sendMessage.setText(category.getName() + " ichidagi kategoriyalar");
            sendMessage.setReplyMarkup(InlineKeyboardUtil.
                    getChildCategories(categories, category));
        }


        DeleteMessage deleteMessage = new DeleteMessage(
                sendMessage.getChatId(), message.getMessageId());
        MY_BOT.sendMsg(deleteMessage);
        MY_BOT.sendMsg(sendMessage);
    }

    public void deleteCategory(String id) {
        sendMessage.setText(adminService.deleteCategory(Integer.valueOf(id)).getMessage());

        DeleteMessage deleteMessage = new DeleteMessage(
                sendMessage.getChatId(), message.getMessageId());
        MY_BOT.sendMsg(deleteMessage);

        MY_BOT.sendMsg(sendMessage);
    }

    public void back(String id) {
        Category category = userService.getCategory(Integer.valueOf(id));
        Category parent = userService.getCategory(category.getParentId());

        if (Objects.isNull(parent)) {
            sendMessage.setText("Kategoriyalar");
            sendMessage.setReplyMarkup(InlineKeyboardUtil.
                    getCategories(userService.getCategories(null)));
        } else {
            sendMessage.setText(parent + "ni kategoriyalari");
            sendMessage.setReplyMarkup(InlineKeyboardUtil.
                    getChildCategories(userService.getCategories(parent.getId()), parent));
        }

        DeleteMessage deleteMessage = new DeleteMessage(
                sendMessage.getChatId(), message.getMessageId());
        MY_BOT.sendMsg(deleteMessage);

        MY_BOT.sendMsg(sendMessage);
    }

    public void editCategory(String id) {
        categoryId.put(sendMessage.getChatId(), Integer.valueOf(id));
        adminStatus.put(sendMessage.getChatId(), AdminStatus.EDIT_CATEGORY);

        sendMessage.setText("Kategoriyaning yangi nomi");
        sendMessage.setReplyMarkup(ReplyKeyboardUtil.getBackButton());

        DeleteMessage deleteMessage = new DeleteMessage(
                sendMessage.getChatId(), message.getMessageId());
        MY_BOT.sendMsg(deleteMessage);
        MY_BOT.sendMsg(sendMessage);
    }

    public void showFavorites() {
        User user = userService.getUserByChatId(sendMessage.getChatId());

        List<Product> favorites = userService.getFavorites(user.getId());

        if (favorites.isEmpty()) {
            sendMessage.setText("Sevimlilar bo'sh");
            sendMessage.setReplyMarkup(ReplyKeyboardUtil.getUserMenu());
            MY_BOT.sendMsg(sendMessage);
            return;
        }

        Product product = favorites.get(order.get(sendMessage.getChatId()));
        SendPhoto sendPhoto = new SendPhoto(sendMessage.getChatId(), new InputFile(product.getPhotoId()));
        Description description = userService.getDescription(product.getId());


        String productText = getCaption(product, description);

        sendPhoto.setCaption(productText);

        sendPhoto.setReplyMarkup(InlineKeyboardUtil.getFavoriteMenu(product.getId(),
                sendMessage.getChatId()));

        MY_BOT.sendMsg(sendPhoto);
    }

    public String getCaption(Product product, Description description) {

        Category category = userService.getCategory(product.getCategoryId());
        StringBuilder sb = new StringBuilder();
        String userPhone = otabek.getUserPhone(product.getUserId());


        sb.append("\uD83D\uDCC4  Nomi:\t").append(category.getName()).append(" ").append(product.getName());
        sb.append("\n");
        sb.append("\uD83D\uDCB0 Narxi: \t").append(product.getPrice()).append("$");
        sb.append("\n");
        sb.append("\uD83E\uDD1D Kami bor: \t").append(product.getNegotiable() ? "✅" : "❌");
        sb.append("\n");
        sb.append("\uD83D\uDCCD Hudud: \t").append(product.getRegion());
        sb.append("\n");
        sb.append("\uD83D\uDCC4 Qo'shimcha ma'lumot: \n").append(description.getText());
        sb.append("\n");
        sb.append("☎️ Kontakt uchun: \t").append(userPhone);
        sb.append(", ").append(description.getPhoneNumber());
        sb.append("\nKo'rganlar soni: ").append(description.getNumberOfView());
        sb.append("\n");
        sb.append("Faolligi: ").append(product.getActive() ? "Faol" : "Faol emas");
        sb.append("\n\nManba: @Avtoadbot");

        return sb.toString();
    }


    public void showMyAds() {
        User user = userService.getUserByChatId(sendMessage.getChatId());
        List<Product> myProducts = userService.getMyProducts(user.getId());

        System.out.println(myProducts);
        if (myProducts.isEmpty()) {
            sendMessage.setText("Sizda e'lonlar mavjud emas");
            sendMessage.setReplyMarkup(ReplyKeyboardUtil.getUserMenu());
            MY_BOT.sendMsg(sendMessage);
        } else {
            Product product = myProducts.get(order.get(sendMessage.getChatId()));
            SendPhoto sendPhoto = new SendPhoto(sendMessage.getChatId(), new InputFile(product.getPhotoId()));

            Description description = userService.getDescription(product.getId());

            String productText = getCaption(product, description);

            sendPhoto.setCaption(productText);

            sendPhoto.setReplyMarkup(InlineKeyboardUtil.getFavoriteMenu(product.getId(),
                    sendMessage.getChatId()));

            MY_BOT.sendMsg(sendPhoto);
        }
    }

}
