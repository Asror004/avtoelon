package com.company.avtoelon.util.keyboardMarkup;

import com.company.avtoelon.container.ComponentContainer;
import com.company.avtoelon.entity.Category;
import com.company.avtoelon.entity.UserCurrentProduct;
import com.company.avtoelon.enums.Region;
import com.company.avtoelon.enums.UserStatus;
import com.company.avtoelon.service.AdminServiceImpl;
import com.company.avtoelon.service.UserServiceImpl;
import com.company.avtoelon.util.InlineKeyboardButtonConstants;
import lombok.NonNull;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

import static com.company.avtoelon.util.InlineKeyboardButtonConstants.*;
import static com.company.avtoelon.util.KeyboardButtonConstants.BACK_ADMIN_MENU;

public class InlineKeyboardUtil {


    private static AdminServiceImpl adminService = new AdminServiceImpl();
    private static UserServiceImpl userService = new UserServiceImpl();


    public static InlineKeyboardMarkup getCategories1(Integer id) {

        List<Category> categories = userService.getCategories(id);

        List<List<InlineKeyboardButton>> lists = new ArrayList<>();


        List<InlineKeyboardButton> buttons = new ArrayList<>();

        int c = 0;

        InlineKeyboardButton button = null;
        for (int i = 0; i < categories.size(); i++) {
            c++;

            button = new InlineKeyboardButton(categories.get(i).getName());
            button.setCallbackData("_category/" + categories.get(i).getId());

            buttons.add(button);


            if (c == 3) {
                lists.add(buttons);
                buttons = new ArrayList<>();
                c = 0;
            }

        }

        if (c != 0)
            lists.add(buttons);


        button = new InlineKeyboardButton(ADD_CATEGORY_CALL_DEMO);
        button.setCallbackData(ADD_CATEGORY_CALLBACK_DEMO + "/" + id);
        buttons = new ArrayList<>();
        buttons.add(button);


        if (id == null) {
            button = new InlineKeyboardButton(HOME_ADMIN_DEMO);
            button.setCallbackData(HOME_ADMIN_CALLBACK_DEMO);
            buttons.add(button);
            lists.add(buttons);
            return new InlineKeyboardMarkup(lists);
        }


        button = new InlineKeyboardButton(BACK_CATEGORY_CALL_DEMO);
        button.setCallbackData(BACK_CATEGORY_CALLBACK_DEMO + "/" + id);
        buttons.add(button);

        lists.add(buttons);

        buttons = new ArrayList<>();

        button = new InlineKeyboardButton(DELETE_CATEGORY_CALL_DEMO);
        button.setCallbackData(DELETE_CATEGORY_CALLBACK_DEMO);
        buttons.add(button);

        button = new InlineKeyboardButton(EDIT_CATEGORY_NAME_DEMO);
        button.setCallbackData(EDIT_CATEGORY_NAME_CALLBACK_DEMO);
        buttons.add(button);

        lists.add(buttons);

        return new InlineKeyboardMarkup(lists);


    }

    public static InlineKeyboardMarkup getExportUserList() {


        InlineKeyboardButton button1 = new InlineKeyboardButton("EXCEL");
        button1.setCallbackData("_excel");

        InlineKeyboardButton button2 = new InlineKeyboardButton("PDF");
        button2.setCallbackData("_pdf");

        List<InlineKeyboardButton> row = List.of(button1, button2);
        List<List<InlineKeyboardButton>> rowList = List.of(row);

        return new InlineKeyboardMarkup(rowList);

    }


    public static InlineKeyboardMarkup getConfirmAndRemove(Integer id) {
        return getMarkup(new ArrayList<>(Arrays.asList(new ArrayList<>
                (Arrays.asList(getButton("Tasdiqlash", String.valueOf(id)),
                        getButton("Qabul qilmaslik", String.valueOf(id) + "r"))))));
    }


    private static InlineKeyboardMarkup getMarkup(List<List<InlineKeyboardButton>> rows) {
        return new InlineKeyboardMarkup(rows);
    }

    private static InlineKeyboardButton getButton(String button, String call) {
        InlineKeyboardButton newButton = new InlineKeyboardButton(button);
        newButton.setCallbackData(call);

        return newButton;
    }


    public static InlineKeyboardMarkup getDelete(Category category) {

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(new ArrayList<>(Arrays.asList(getButton(category.getName() + "ni o'chirish",
                "delete_category/" + category.getId()))));
        rows.add(new ArrayList<>(Arrays.asList(
                getButton(category.getName() + "ni tahrirlash", "edit_category/" + category.getId()))));
        rows.add(new ArrayList<>(Arrays.asList(getButton(BACK_ADMIN_MENU,
                "back/" + category.getId()))));

        return getMarkup(rows);
    }


    public static ReplyKeyboard getCategoriesUser(Integer parentId) {

        Category categoryById = adminService.getCategoryById(parentId);
        List<Category> categories = userService.getCategories(parentId);


        List<List<InlineKeyboardButton>> lists = new ArrayList<>();

        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();

        InlineKeyboardButton button = new InlineKeyboardButton();


        int c = 0;

        for (int i = 0; i < categories.size(); i++) {
            c++;

            button = new InlineKeyboardButton(categories.get(i).getName());
            button.setCallbackData("_user_category/" + categories.get(i).getId());

            inlineKeyboardButtons.add(button);

            if (c == 3) {
                lists.add(inlineKeyboardButtons);
                inlineKeyboardButtons = new ArrayList<>();
                c = 0;
            }

        }

        if (!inlineKeyboardButtons.isEmpty()) {
            lists.add(inlineKeyboardButtons);
        }
        inlineKeyboardButtons = new ArrayList<>();


        button = new InlineKeyboardButton(SEARCH_DEMO);
        button.setCallbackData(SEARCH_CALLBACK);
        inlineKeyboardButtons.add(button);


        if (parentId == null) {
            button = new InlineKeyboardButton(HOME_ADMIN_DEMO);
            button.setCallbackData(HOME_USER_CALLBACK);
            inlineKeyboardButtons.add(button);
            lists.add(inlineKeyboardButtons);
            return new InlineKeyboardMarkup(lists);
        }


        button = new InlineKeyboardButton(InlineKeyboardButtonConstants.BACK_USER_DEMO);
        button.setCallbackData(BACK_USER_CALLBACK + "/" + categoryById.getId());
        inlineKeyboardButtons.add(button);
        lists.add(inlineKeyboardButtons);

        return new InlineKeyboardMarkup(lists);


    }

    public static InlineKeyboardMarkup getRegions(Region region, String chatId) {

        List<List<InlineKeyboardButton>> lists = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();


        EnumSet<Region> enumSet = EnumSet.allOf(Region.class);


        List<String> list = ComponentContainer.user_current_search_regions.get(chatId);


        if (list.contains(region.toString())) {
            list.remove(region.toString());
        } else {
            if (list.contains(Region.ALL.toString())) {
                list.remove(Region.ALL.toString());
            }
            if (list.size() < 5)
                list.add(region.toString());
        }

        System.out.println("list = " + list);
        System.out.println("ComponentContainer.user_current_search_regions.get(chatId) = " + ComponentContainer.user_current_search_regions.get(chatId));

        if (region == Region.ALL && list.size() != 1) {
            List<String> list1 = new ArrayList<>();
            list1.add(region.toString());

            ComponentContainer.user_current_search_regions.put(chatId, list1);
            list = ComponentContainer.user_current_search_regions.get(chatId);
        }

        if (list.isEmpty()) {
            list.add(Region.ALL.toString());
        }


        int c = 0;

        for (Region reg : enumSet) {

            c++;
            if (list.contains(reg.toString())) {
                button = new InlineKeyboardButton("\uD83D\uDFE2 " + reg);
            } else {
                button = new InlineKeyboardButton(reg.toString());
            }
            button.setCallbackData("_region/" + reg);


            inlineKeyboardButtons.add(button);

            if (c == 3) {
                lists.add(inlineKeyboardButtons);
                inlineKeyboardButtons = new ArrayList<>();
                c = 0;
            }

        }


        lists.add(inlineKeyboardButtons);

        inlineKeyboardButtons = new ArrayList<>();

        button = new InlineKeyboardButton(SEARCH_DEMO);
        button.setCallbackData(SEARCH_REGION_CALL_BACK);
        inlineKeyboardButtons.add(button);

        lists.add(inlineKeyboardButtons);

        return new InlineKeyboardMarkup(lists);
    }

    public static InlineKeyboardMarkup getCurrentAd(UserCurrentProduct userCurrentProduct) {


        List<List<InlineKeyboardButton>> lists = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();

        if (userCurrentProduct.getOrdinal() != 1) {
            button = new InlineKeyboardButton(PREV_AD_DEMO);
            button.setCallbackData(PREV_AD_CALLBACK);
            inlineKeyboardButtons.add(button);
        }

        if (userCurrentProduct.getOrdinal() != userCurrentProduct.getProductDescriptions().size()) {
            button = new InlineKeyboardButton(InlineKeyboardButtonConstants.NEXT_AD_DEMO);
            button.setCallbackData(NEXT_AD_CALLBACK);
            inlineKeyboardButtons.add(button);
        }

        button = new InlineKeyboardButton(HOME_ADMIN_DEMO);
        button.setCallbackData(HOME_USER_CALLBACK);
        inlineKeyboardButtons.add(button);
        lists.add(inlineKeyboardButtons);

        button = new InlineKeyboardButton(ADD_FAVORITE_DEMO);
        button.setCallbackData(ADD_FAVORITE_CALLBACK);
        inlineKeyboardButtons = new ArrayList<>();
        inlineKeyboardButtons.add(button);
        lists.add(inlineKeyboardButtons);

        return new InlineKeyboardMarkup(lists);

    }

    public static ReplyKeyboard getUserBackHome() {

        List<InlineKeyboardButton> list = new ArrayList<>();

        InlineKeyboardButton button = new InlineKeyboardButton(HOME_ADMIN_DEMO);
        button.setCallbackData(HOME_USER_CALLBACK);
        list.add(button);
        button = new InlineKeyboardButton(InlineKeyboardButtonConstants.CONTINUE_SEARCHING_DEMO);
        button.setCallbackData(CONTINUE_SEARCHING_CALLBACK);
        list.add(button);
        return new InlineKeyboardMarkup(Arrays.asList(list));
    }

    public static InlineKeyboardMarkup getPriceField(String chatId, String sign) {

        ComponentContainer.userCurrentProductPriceInterval.merge(chatId, sign, (p1, p2) -> p1 + p2);


        List<List<InlineKeyboardButton>> lists = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();

        InlineKeyboardButton button = new InlineKeyboardButton(ComponentContainer.userCurrentProductPriceInterval.get(chatId));
        button.setCallbackData("q");
        inlineKeyboardButtons.add(button);
        lists.add(inlineKeyboardButtons);
        inlineKeyboardButtons = new ArrayList<>();

        int c = 0;
        for (int i = 0; i <= 9; i++) {
            c++;
            button = new InlineKeyboardButton(String.valueOf(i));
            button.setCallbackData("_number/" + i);

            inlineKeyboardButtons.add(button);

            if (c == 3) {
                lists.add(inlineKeyboardButtons);
                inlineKeyboardButtons = new ArrayList<>();
                c = 0;
            }

        }

        button = new InlineKeyboardButton(ERASE_LAST_SIGN);
        button.setCallbackData(ERASE_LAST_SIGN_CALLBACK);
        inlineKeyboardButtons = new ArrayList<>();

        inlineKeyboardButtons.add(button);

        button = new InlineKeyboardButton(InlineKeyboardButtonConstants.MINUS_DEMO);
        button.setCallbackData(MINUS_CALLBACK);
        inlineKeyboardButtons.add(button);

        lists.add(inlineKeyboardButtons);

        inlineKeyboardButtons = new ArrayList<>();

        button = new InlineKeyboardButton(SEARCH_DEMO);
        button.setCallbackData(InlineKeyboardButtonConstants.SEARCH_PRICE_CALLBACK);
        inlineKeyboardButtons.add(button);

        lists.add(inlineKeyboardButtons);


        return new InlineKeyboardMarkup(lists);

    }

    public static ReplyKeyboard getUserAdCategories(Integer catId) {


        List<List<InlineKeyboardButton>> lists = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        InlineKeyboardButton button;


        List<Category> categories = userService.getCategories(catId);


        if (categories.isEmpty()) {

            button = new InlineKeyboardButton(BACK_USER_DEMO);
            button.setCallbackData(BACK_USER_AD_CALLBACK + "/" + catId);
            inlineKeyboardButtons.add(button);

            button = new InlineKeyboardButton(InlineKeyboardButtonConstants.PUT_AD_DEMO);
            button.setCallbackData("_put_ad" + "/" + catId);
            inlineKeyboardButtons.add(button);

            lists.add(inlineKeyboardButtons);

            return new InlineKeyboardMarkup(lists);

        } else {


            int c = 0;

            for (int i = 0; i < categories.size(); i++) {

                c++;

                button = new InlineKeyboardButton(categories.get(i).getName());
                button.setCallbackData(PUT_AD_CALLBACK + "/" + categories.get(i).getId());

                inlineKeyboardButtons.add(button);

                if (c == 3) {
                    lists.add(inlineKeyboardButtons);
                    inlineKeyboardButtons = new ArrayList<>();
                    c = 0;
                }

            }


            if (!inlineKeyboardButtons.isEmpty()) {
                lists.add(inlineKeyboardButtons);
            }


            inlineKeyboardButtons = new ArrayList<>();

            button = new InlineKeyboardButton(BACK_USER_DEMO);
            button.setCallbackData(BACK_USER_AD_CALLBACK + "/" + catId);
            inlineKeyboardButtons.add(button);
            lists.add(inlineKeyboardButtons);
            return new InlineKeyboardMarkup(lists);

        }


    }

    public static InlineKeyboardMarkup getIsNegotiable() {

        List<List<InlineKeyboardButton>> lists = new ArrayList<>();

        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();

        InlineKeyboardButton button = new InlineKeyboardButton("✅");
        button.setCallbackData("_negotiable");
        inlineKeyboardButtons.add(button);

        button = new InlineKeyboardButton("❌");
        button.setCallbackData("_not_negotiable");
        inlineKeyboardButtons.add(button);

        lists.add(inlineKeyboardButtons);

        return new InlineKeyboardMarkup(lists);
    }

    public static ReplyKeyboard getAdLocation() {

        List<List<InlineKeyboardButton>> lists = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        InlineKeyboardButton button;


        int c = 0;

        EnumSet<Region> enumSet = EnumSet.allOf(Region.class);


        for (Region region : enumSet) {
            c++;

            if (region.equals(Region.ALL))
                continue;
            button = new InlineKeyboardButton(region.toString());
            button.setCallbackData("ad_location/" + region);

            inlineKeyboardButtons.add(button);

            if (c == 3) {
                lists.add(inlineKeyboardButtons);
                inlineKeyboardButtons = new ArrayList<>();
                c = 0;
            }
        }

        if (!inlineKeyboardButtons.isEmpty()) {
            lists.add(inlineKeyboardButtons);
        }


        return new InlineKeyboardMarkup(lists);
    }

    public static ReplyKeyboard confirmAdUser() {

        List<List<InlineKeyboardButton>> lists = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();

        InlineKeyboardButton button = new InlineKeyboardButton("✅");
        button.setCallbackData("_confirm_ad_user");
        inlineKeyboardButtons.add(button);

        button = new InlineKeyboardButton("❌");
        button.setCallbackData("_cancel_ad_user");
        inlineKeyboardButtons.add(button);

        lists.add(inlineKeyboardButtons);


        return new InlineKeyboardMarkup(lists);
    }

    public static ReplyKeyboard getFavoriteMenu(Integer id, @NonNull String chatId) {
        UserServiceImpl userService = new UserServiceImpl();
        Integer userId = userService.getUserByChatId(chatId).getId();
        List<InlineKeyboardButton> row1 = new ArrayList<>(Arrays.asList(
                getButton("O'chirish❌", "r/" + id + "/" + userId)
        ));


        List<InlineKeyboardButton> row2 = new ArrayList<>();

        if (!ComponentContainer.order.get(chatId).equals(0))
            row2.add(getButton("Orqaga", "back"));
        if (ComponentContainer.userStatus.get(chatId).equals(UserStatus.SHOW_FAVORITES) &&
                !ComponentContainer.order.get(chatId).equals(userService.
                        getFavorites(userId).size() - 1))
            row2.add(getButton("Oldinga", "next"));
        else if (ComponentContainer.userStatus.get(chatId).equals(UserStatus.SHOW_MY_PRODUCTS) &&
                !ComponentContainer.order.get(chatId).equals(userService.
                        getMyProducts(userId).size() - 1)) {
            row2.add(getButton("Oldinga", "next"));
        }

        List<InlineKeyboardButton> row3 = new ArrayList<>(Arrays.asList(
                getButton("Asosiy menyu", "main")
        ));

        return getMarkup(new ArrayList<>(Arrays.asList(row1, row2, row3)));
    }


    public static InlineKeyboardMarkup getCategories(List<Category> categoryList) {
        int counter = 0;

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        for (Category category : categoryList) {
            counter++;
            if (counter > 3) {
                rows.add(row);
                row = new ArrayList<>();

                counter = 0;
            }

            InlineKeyboardButton button = getButton(category.getName(), String.valueOf(category.getId()));
            row.add(button);
        }

        if (counter < 4) {
            rows.add(row);
        }

        rows.add(new ArrayList<>(Arrays.asList(getButton(BACK_ADMIN_MENU, "menu"))));

        return getMarkup(rows);
    }

    public static InlineKeyboardMarkup getChildCategories(List<Category> categoryList, Category category1) {

        int counter = 0;

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();

        for (Category category : categoryList) {
            counter++;

            if (counter == 4) {
                rows.add(row);
                row = new ArrayList<>();

                counter = 0;
            }

            InlineKeyboardButton button = getButton(category.getName(), String.valueOf(category.getId()));
            row.add(button);

        }

        if (counter < 4) {
            rows.add(row);
        }

        rows.add(new ArrayList<>(Arrays.asList(getButton(category1.getName() +
                "ni tahrirlash", "edit_category/" + category1.getId()))));
        rows.add(new ArrayList<>(Arrays.asList(getButton(BACK_ADMIN_MENU,
                "back/" + category1.getId()))));

        return getMarkup(rows);

    }
}
