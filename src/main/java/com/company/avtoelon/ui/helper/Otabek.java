package com.company.avtoelon.ui.helper;

import com.company.avtoelon.container.ComponentContainer;
import com.company.avtoelon.controller.AdminController;
import com.company.avtoelon.entity.Category;
import com.company.avtoelon.entity.ProductDescription;
import com.company.avtoelon.entity.UserCurrentProduct;
import com.company.avtoelon.enums.AdminStatus;
import com.company.avtoelon.enums.Region;
import com.company.avtoelon.payload.Result;
import com.company.avtoelon.service.AdminServiceImpl;
import com.company.avtoelon.service.UserServiceImpl;
import com.company.avtoelon.util.ConnectionUtil;
import com.company.avtoelon.util.keyboardMarkup.InlineKeyboardUtil;
import com.company.avtoelon.util.keyboardMarkup.ReplyKeyboardUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.sql.*;
import java.util.*;
import java.util.regex.Pattern;

public class Otabek {


    private static UserServiceImpl userService = new UserServiceImpl();
    private static AdminServiceImpl adminService = new AdminServiceImpl();
    private static Connection connection = ConnectionUtil.getConnection();


    SendMessage sendMessage = new SendMessage();

    public static Category getParentCategory(Integer childId) {


        return null;


    }

    public void sendCategoryName(String chatId, String text) {

        Category category = new Category(text, ComponentContainer.admin_current_category.get(chatId));
        Result result = adminService.addCategory(category);
        Integer parentId = category.getParentId();
        Category parent = adminService.getCategoryById(parentId);

        sendMessage.setText(result.getMessage() + "\n" + (parent != null ? parent.getName() : ""));
        sendMessage.setReplyMarkup(InlineKeyboardUtil.getCategories1(
                ComponentContainer.admin_current_category.get(chatId
                )));

        if (result.isSuccess())
            ComponentContainer.categories = userService.getAllCategories();

        sendMessage.setChatId(chatId);
        ComponentContainer.MY_BOT.sendMsg(sendMessage);

    }

    public void renameCategory(String text, String chatId) {

        Result result = adminService.editCategoryName(ComponentContainer.admin_current_category.get(chatId),
                text);

        if (result.isSuccess())
            ComponentContainer.categories = userService.getAllCategories();

        Category category = adminService.getCategoryById(ComponentContainer.admin_current_category.get(chatId));

        sendMessage.setText(result.getMessage() + "\n" + category.getName());
        sendMessage.setReplyMarkup(InlineKeyboardUtil.getCategories1(category.getId()));

        sendMessage.setChatId(chatId);
        ComponentContainer.MY_BOT.sendMsg(sendMessage);
    }

    public void blockUser(String text, String chatId) {

        boolean correct = Pattern.matches("^(\\+998)?\\d{9}$", text);

        if (!correct) {
            sendMessage.setText("Telefon formati noto'g'ri.");
        } else {

            try {

                CallableStatement callableStatement = ConnectionUtil.getConnection()
                        .prepareCall("{call block_users(?,?,?)}");
                callableStatement.setString(1, text);
                callableStatement.registerOutParameter(2, Types.VARCHAR);
                callableStatement.registerOutParameter(3, Types.BOOLEAN);

                callableStatement.execute();

                String func_message = callableStatement.getString(2);
                boolean func_bool = callableStatement.getBoolean(3);

                sendMessage.setText(func_message);


            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        ComponentContainer.admin_wish.remove(chatId);
        sendMessage.setReplyMarkup(ReplyKeyboardUtil.getAdminMenuTwo());
        sendMessage.setChatId(chatId);
        ComponentContainer.MY_BOT.sendMsg(sendMessage);

    }

    public void addAdmin(String text, String chatId) {

        Result result = adminService.addAdmin(text);
        sendMessage.setText(result.getMessage());
        sendMessage.setReplyMarkup(ReplyKeyboardUtil.getAdminMenuTwo());
        sendMessage.setChatId(chatId);
        ComponentContainer.MY_BOT.sendMsg(sendMessage);
    }


    ////////////////////////////////////////////


    public void addCategoryCall(String chatId) {

        ComponentContainer.categories = userService.getAllCategories();
        ComponentContainer.admin_wish.put(chatId, AdminStatus.SEND_CATEGORY_NAME);
        sendMessage.setText("Nom kiriting");
        sendMessage.setChatId(chatId);
        ComponentContainer.MY_BOT.sendMsg(sendMessage);
    }

    public void categoryCall(String data, String chatId) {

        ComponentContainer.categories = userService.getAllCategories();

        Integer id = Integer.valueOf(data.split("/")[1]);
        ComponentContainer.admin_current_category.put(chatId, id);

        System.out.println(ComponentContainer.categories);
        Category category = adminService.getCategoryById(id);
        System.out.println(category);
//            Category parent=adminService.getCategoryById(category.getParentId());
        ComponentContainer.admin_current_category.put(chatId, id);
        sendMessage.setText(category.getName());
        sendMessage.setReplyMarkup(InlineKeyboardUtil.getCategories1(id));
        sendMessage.setChatId(chatId);
        ComponentContainer.MY_BOT.sendMsg(sendMessage);
    }

    public void backCategoryCall(String data, String chatId) {
        //            ComponentContainer.categories = userService.getAllCategories();

        Integer id = ComponentContainer.admin_current_category.get(chatId);
        Category category = adminService.getCategoryById(id);


        if (category.getParentId() != 0) {
            Category parent = adminService.getCategoryById(category.getParentId());

            ComponentContainer.admin_current_category.put(chatId, parent.getId());
            System.out.println(category);
            if (category.getParentId() != null) {
                sendMessage.setText(parent.getName());
                sendMessage.setReplyMarkup(InlineKeyboardUtil.getCategories1(parent.getId()));  // exc
            }

        } else {
            sendMessage.setText("Kategoriyalar");
            sendMessage.setReplyMarkup(InlineKeyboardUtil.getCategories1(null));
            ComponentContainer.admin_current_category.put(chatId, null);
        }

        sendMessage.setChatId(chatId);
        ComponentContainer.MY_BOT.sendMsg(sendMessage);
    }

    public void deleteCategoryCall(String data, String chatId) {


        Integer id = ComponentContainer.admin_current_category.get(chatId);
        Category category = adminService.getCategoryById(id);

        Result result = adminService.deleteCategory(id);


        if (!result.isSuccess()) {
            sendMessage.setText(result.getMessage() + "\n" + category.getName());
            sendMessage.setReplyMarkup(InlineKeyboardUtil.getCategories1(id));

        } else {
            Category parent = adminService.getCategoryById(category.getParentId());
            sendMessage.setText(result.getMessage() + "\n" + (parent != null ? parent.getName() : ""));
            sendMessage.setReplyMarkup(InlineKeyboardUtil.getCategories1(parent != null ? parent.getId() : null));
            ComponentContainer.admin_current_category.put(chatId, (parent == null ? null : parent.getId()));
            ComponentContainer.categories = userService.getAllCategories();
        }

        sendMessage.setChatId(chatId);

        ComponentContainer.MY_BOT.sendMsg(sendMessage);

    }


    public void changeUserCurrentCategory(String data, Message message, User user) {

        String chatId = String.valueOf(message.getChatId());
        Integer categoryId = Integer.valueOf(data.split("/")[1]);

        Category category = adminService.getCategoryById(categoryId);

        ComponentContainer.user_current_category.put(chatId, categoryId);

        sendMessage.setText(category.getName());
        sendMessage.setReplyMarkup(InlineKeyboardUtil.getCategoriesUser(categoryId));
        sendMessage.setChatId(chatId);
        ComponentContainer.MY_BOT.sendMsg(sendMessage);
    }

    public void backUserCategory(String data, Message message, String chatId) {


        sendMessage.setChatId(chatId);
        Integer categoryId = Integer.valueOf(data.split("/")[1]);

        Category categoryById = adminService.getCategoryById(categoryId);

        Category parent = adminService.getCategoryById(categoryById.getParentId());


        if (parent == null) {
            sendMessage.setText("Kategoriyalar");
            sendMessage.setReplyMarkup(InlineKeyboardUtil.getCategoriesUser(null));
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
            AdminController.deleteInline(message, chatId);
            ComponentContainer.user_current_category.put(chatId, parent.getId());
        }


        sendMessage.setText(parent.getName());
        sendMessage.setReplyMarkup(InlineKeyboardUtil.getCategoriesUser(parent.getId()));
        ComponentContainer.MY_BOT.sendMsg(sendMessage);

        ComponentContainer.user_current_category.put(chatId, parent.getId());

    }

    public void getSearchedProducts(String chatId, int start, int end) {

        List<Integer> categoryProducts = null;


        ComponentContainer.categories = userService.getAllCategories();

        Integer integer = ComponentContainer.user_current_category.get(chatId);

        if (integer == null)
            integer = 0;

        categoryProducts = getCategoryProducts(integer, new ArrayList<>());


        System.out.println(categoryProducts);
        sendMessage.setChatId(chatId);

        List<ProductDescription> productList = filterProducts("date", categoryProducts, chatId, start, end);
        System.out.println(productList);

        if (productList.isEmpty()) {
            sendMessage.setText("Ushbu qidiruv bo'yicha hech qanday ma'lumot topilmadi \uD83D\uDE15");
            sendMessage.setReplyMarkup(InlineKeyboardUtil.getUserBackHome());
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
            return;
        }

        ComponentContainer.userCurrentProduct.put(chatId, new UserCurrentProduct(productList, 1));

        System.out.println(productList);
        SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(productList.get(0).getPhoto_id()));
        sendPhoto.setCaption(getCaption(productList.get(0)));


        sendPhoto.setReplyMarkup(InlineKeyboardUtil.getCurrentAd(ComponentContainer.userCurrentProduct.get(chatId)));
        ComponentContainer.MY_BOT.sendMsg(sendPhoto);
    }

    public String getCaption(ProductDescription productDescription) {

        StringBuilder sb = new StringBuilder();
        sb.append("\uD83D\uDCC4 Nomi:\t" + productDescription.getName());
        sb.append("\n");
        sb.append("\uD83D\uDCB0 Narxi: \t" + productDescription.getPrice() + "$");
        sb.append("\n");
        sb.append("\uD83E\uDD1D Kami bor: \t" + (productDescription.isNegotiable() ? "✅" : "❌"));
        sb.append("\n");
        sb.append("\uD83D\uDCCD Hudud: \t" + productDescription.getRegion());
        sb.append("\n");
        sb.append("\uD83D\uDCC4 Qo'shimcha ma'lumot: \t" + productDescription.getInfo_());
        sb.append("\n");
        sb.append("☎️ Kontakt uchun: \t" + getUserPhone(productDescription.getUser_id()));
        sb.append(" " + productDescription.getPhone_number());
        sb.append("\n");
        sb.append("\uD83D\uDC41\u200D\uD83D\uDDE8 Ko'rildi: \t" + (productDescription.getViews() + 1));
        sb.append("\n\nManba: @Avtoadbot");


        return sb.toString();
    }

    public String getUserPhone(Integer user_id) {

        String sql = "{call get_user_by_id(?,?)}";

        try (CallableStatement callableStatement = connection.prepareCall(sql)) {

            callableStatement.setInt(1, user_id);

            callableStatement.registerOutParameter(2,Types.VARCHAR);
            callableStatement.execute();


            return callableStatement.getString(2);


        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "";

    }

    private List<Integer> getAllCategoryId() {
        return ComponentContainer.categories.stream()
                .filter(category -> category.getParentId() != null)
                .map(category -> category.getId())
                .toList();
    }

    private List<ProductDescription> filterProducts(String orderBy, List<Integer> catList, String chatId, int start, int end) {

        userService.getProductDescription();

        System.out.println("ComponentContainer.productDescriptions = " + ComponentContainer.productDescriptions);

        System.out.println("catList = " + catList);
        List<ProductDescription> productDescriptions = sortToRegCat(catList, chatId);

        System.out.println("productDescriptions = " + productDescriptions);


        if (orderBy.startsWith("date")) {


            if (orderBy.endsWith("desc")) {

                return productDescriptions.stream()
                        .filter(productDescription -> productDescription.getPrice() >= start &&
                                productDescription.getPrice() <= end)
                        .sorted((p1, p2) -> p2.getCreated_at().compareTo(p1.getCreated_at())).toList();

            } else {
                return productDescriptions.stream()
                        .filter(productDescription -> productDescription.getPrice() >= start &&
                                productDescription.getPrice() <= end)
                        .sorted(Comparator.comparing(ProductDescription::getCreated_at)).toList();
            }

        } else if (orderBy.startsWith("price")) {


            if (orderBy.endsWith("desc")) {
                return productDescriptions.stream()
                        .filter(productDescription -> productDescription.getPrice() >= start &&
                                productDescription.getPrice() <= end)
                        .sorted(Comparator.comparingDouble(ProductDescription::getPrice).reversed()).toList();
            } else {
                return productDescriptions.stream()
                        .filter(productDescription -> productDescription.getPrice() >= start &&
                                productDescription.getPrice() <= end)
                        .sorted(Comparator.comparingDouble(ProductDescription::getPrice)).toList();
            }


        }
        return productDescriptions;
    }


    private List<ProductDescription> sortToRegCat(List<Integer> catList, String chatId) {

        List<ProductDescription> productDescriptions = new ArrayList<>();

        System.out.println("catList = " + catList);


        List<String> regions = new ArrayList<>();

        System.out.println("ComponentContainer.user_current_search_regions = " + ComponentContainer.user_current_search_regions);


        if (ComponentContainer.user_current_search_regions.get(chatId).get(0).equals(Region.ALL.toString())) {
            for (Region value : Region.values()) {
                regions.add(value.toString());
            }

        } else {
            regions.addAll(ComponentContainer.user_current_search_regions.get(chatId));
        }

        System.out.println("productDescriptions = " + productDescriptions);
        System.out.println("ComponentContainer.productDescriptions = " + ComponentContainer.productDescriptions);
        for (ProductDescription productDescription : ComponentContainer.productDescriptions) {

            if (catList.contains(productDescription.getCategory_id()) &&
                    regions.contains(productDescription.getRegion())) {

                productDescriptions.add(productDescription);

            }
            System.out.println("catList = " + catList);
            System.out.println("productDescription.getCategory_id() = " + productDescription.getCategory_id());
            System.out.println();
        }

        return productDescriptions;
    }


    List<Integer> getCategoryProducts(Integer parentId, List<Integer> list) {

        List<Integer> childList = getChildList(parentId);

        System.out.println("childList = " + childList);

        if (childList.isEmpty()) {
            list.add(parentId);
        } else {
            for (Integer integer : childList) {
                getCategoryProducts(integer, list);
            }
        }

        return list;
    }

    private List<Integer> getChildList(Integer parentId) {
        ComponentContainer.categories.forEach(System.out::println);
        return ComponentContainer.categories.stream()
                .filter(category -> category.getParentId().equals(parentId))
                .map(Category::getId)
                .toList();

    }

    public void nextAd(String chatId) {

        UserCurrentProduct userCurrentProduct = ComponentContainer.userCurrentProduct.get(chatId);
        userCurrentProduct.setOrdinal(userCurrentProduct.getOrdinal() + 1);

        int ordinal = userCurrentProduct.getOrdinal() - 1;

        System.out.println("userCurrentProduct.getOrdinal() = " + userCurrentProduct.getOrdinal());

        SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(userCurrentProduct.getProductDescriptions().get(ordinal).getPhoto_id()));
        sendPhoto.setCaption(getCaption(userCurrentProduct.getProductDescriptions().get(ordinal)));
        sendPhoto.setReplyMarkup(InlineKeyboardUtil.getCurrentAd(userCurrentProduct));
        ComponentContainer.MY_BOT.sendMsg(sendPhoto);
    }

    public void prevAd(String chatId) {

        UserCurrentProduct userCurrentProduct = ComponentContainer.userCurrentProduct.get(chatId);
        userCurrentProduct.setOrdinal(userCurrentProduct.getOrdinal() - 1);
        int ordinal = userCurrentProduct.getOrdinal() - 1;

        SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(userCurrentProduct.getProductDescriptions().get(ordinal).getPhoto_id()));
        sendPhoto.setCaption(getCaption(userCurrentProduct.getProductDescriptions().get(userCurrentProduct.getOrdinal())));
        sendPhoto.setReplyMarkup(InlineKeyboardUtil.getCurrentAd(userCurrentProduct));
        ComponentContainer.MY_BOT.sendMsg(sendPhoto);

    }

    public void addFavorite(String chatId) {

        UserCurrentProduct userCurrentProduct = ComponentContainer.userCurrentProduct.get(chatId);

        ProductDescription productDescription = userCurrentProduct.getProductDescriptions().get(userCurrentProduct.getOrdinal() - 1);

        com.company.avtoelon.entity.User userByChatId = userService.getUserByChatId(chatId);
        Result result = userService.addFavorite(productDescription.getId(), userByChatId.getId());

        System.out.println(result);

        sendMessage.setChatId(chatId);
        sendMessage.setText(result.getMessage());
        ComponentContainer.MY_BOT.sendMsg(sendMessage);
    }

    public void homeUser(String chatId) {

        ComponentContainer.userCurrentProduct.remove(chatId);
        ComponentContainer.user_current_search_regions.remove(chatId);
        ComponentContainer.userCurrentProduct.remove(chatId);

        sendMessage.setText("Menu");
        sendMessage.setReplyMarkup(ReplyKeyboardUtil.getUserMenu());
        sendMessage.setChatId(chatId);
        ComponentContainer.MY_BOT.sendMsg(sendMessage);
    }

    public void getPriceInterval(String chatId) {

        sendMessage.setText("Narxni kiriting (5000-15000)");
        sendMessage.setReplyMarkup(InlineKeyboardUtil.getPriceField(chatId, ""));
        sendMessage.setChatId(chatId);
        ComponentContainer.MY_BOT.sendMsg(sendMessage);
    }

    public void eraseLastSign(String chatId) {

        String s = ComponentContainer.userCurrentProductPriceInterval.get(chatId);
        if (s.length() != 0) {
            s = s.substring(0, s.length() - 1);
        }

        ComponentContainer.userCurrentProductPriceInterval.put(chatId, s);
    }

    public boolean validatePriceInterval(String chatId) {

        String message = "";
        sendMessage.setChatId(chatId);

        String s = ComponentContainer.userCurrentProductPriceInterval.get(chatId);


        String[] split = s.split("-");

        if (split.length != 2) {
            message = "Kiritilgan format noto'gri";
            sendMessage.setText(message);
            sendMessage.setChatId(chatId);
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
            return false;
        }

        try {

            int start = Integer.parseInt(split[0]);
            int end = Integer.parseInt(split[1]);

            if (start >= end) {
                message = "Ilk qiymat katta bo'la olmaydi";
                sendMessage.setText(message);
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            } else {
                getSearchedProducts(chatId, start, end);
                return true;
            }


        } catch (Exception ex) {
            ex.printStackTrace();
            message = "Kiritilgan format noto'gri (500-1000)";
            return false;
        }

        if (message.length() > 0) {
            sendMessage.setText(message);
            ComponentContainer.MY_BOT.sendMsg(message);
            return false;
        }
        return false;
    }

    public void putMinus(String chatId) {

        ComponentContainer.userCurrentProductPriceInterval.merge(chatId, "-", (p1, p2) -> p1 + p2);

    }

    public void addNumber(String chatId, Message message, String data) {

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setMessageId(message.getMessageId());
        editMessageReplyMarkup.setChatId(chatId);

        editMessageReplyMarkup.setReplyMarkup(InlineKeyboardUtil.getPriceField(chatId, data.split("/")[1]));
        ComponentContainer.MY_BOT.sendMsg(editMessageReplyMarkup);
    }

    public void eraseLastSignExecute(Message message, String chatId) {

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setMessageId(message.getMessageId());
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setReplyMarkup(InlineKeyboardUtil.getPriceField(chatId, ""));
        ComponentContainer.MY_BOT.sendMsg(editMessageReplyMarkup);
    }

    public void minusExecute(Message message, String chatId) {

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setMessageId(message.getMessageId());
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setReplyMarkup(InlineKeyboardUtil.getPriceField(chatId, ""));
        ComponentContainer.MY_BOT.sendMsg(editMessageReplyMarkup);
    }

    public String getCaption2(ProductDescription productDescription) {


        StringBuilder sb = new StringBuilder();
        Otabek otabek = new Otabek();
        String userPhone = otabek.getUserPhone(productDescription.getUser_id());

        Category category = userService.getCategory(productDescription.getCategory_id());
        sb.append("\uD83D\uDCC4  Nomi:\t").append(category.getName()).append(" ").append(productDescription.getName());
        sb.append("\n");
        sb.append("\uD83D\uDCB0 Narxi: \t").append(productDescription.getPrice()).append("$");
        sb.append("\n");
        sb.append("\uD83E\uDD1D Kami bor: \t").append(productDescription.isNegotiable() ? "✅" : "❌");
        sb.append("\n");
        sb.append("\uD83D\uDCCD Hudud: \t").append(productDescription.getRegion());
        sb.append("\n");
        sb.append("\uD83D\uDCC4 Qo'shimcha ma'lumot: \n").append(productDescription.getInfo_());
        sb.append("\n");
        sb.append("☎️ Kontakt uchun: \t").append(userPhone);
        sb.append(" ").append(productDescription.getPhone_number());
        sb.append("\n");
        sb.append("\n\nManba: @Avtoadbot");


        return sb.toString();
    }
}
