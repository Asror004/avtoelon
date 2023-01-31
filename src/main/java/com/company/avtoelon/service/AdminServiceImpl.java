package com.company.avtoelon.service;

import com.company.avtoelon.container.ComponentContainer;
import com.company.avtoelon.entity.Category;
import com.company.avtoelon.entity.Product;
import com.company.avtoelon.entity.User;
import com.company.avtoelon.payload.Result;
import com.company.avtoelon.util.ConnectionUtil;
import com.google.gson.Gson;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import lombok.AllArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AdminServiceImpl implements AdminService {

    UserServiceImpl userService = new UserServiceImpl();
    List<User> users = getUsers();

    @Override
    public File getUsersPDF() {

        File file = new File("src/main/resources/users.pdf");

        try (PdfWriter writer = new PdfWriter(file);
             PdfDocument pdfDocument = new PdfDocument(writer);
             Document document = new Document(pdfDocument)) {

            List<User> users = getUsers();

            pdfDocument.addNewPage();

            float[] pointColumnWidths = {20F, 150F, 140F, 80F};

            Table table = new Table(pointColumnWidths);
            table.addCell("id");
            table.addCell("F.I.SH");
            table.addCell("Telefon raqam");
            table.addCell("Bloklanganligi");

            for (User user : users) {
                table.addCell(String.valueOf(user.getId()));
                table.addCell(user.getFullName());
                table.addCell(user.getPhoneNumber());
                if (user.getActive())
                    table.addCell("Yo'q");
                else
                    table.addCell("Ha");
            }

            document.add(table);

            return file;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    @Override
    public File getUsersEXCEL() {

        File file = new File("src/main/resources/users.xlsx");
        List<User> users = getUsers();

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             FileOutputStream out = new FileOutputStream(file)) {

            XSSFSheet spreadsheet = workbook.createSheet("users");
            XSSFRow row = spreadsheet.createRow(1);

            row.createCell(1).setCellValue("id");
            row.createCell(2).setCellValue("F.I.SH");
            row.createCell(3).setCellValue("Telefon raqam");
            row.createCell(4).setCellValue("Bloklanganligi");

            for (int i = 0; i < users.size(); i++) {
                XSSFRow row2 = spreadsheet.createRow(i + 2);

                row2.createCell(1).setCellValue(String.valueOf(users.get(i).getId()));
                row2.createCell(2).setCellValue(users.get(i).getFullName());
                row2.createCell(3).setCellValue(users.get(i).getPhoneNumber());
                if (users.get(i).getActive())
                    row2.createCell(4).setCellValue("Yo'q");
                else
                    row2.createCell(4).setCellValue("Ha");
            }

            for (int i = 0; i < 4; i++) {
                spreadsheet.autoSizeColumn(i);
            }

            workbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    @Override
    public List<User> getUsers() {


        String sql = "{call get_Users()}";
        List<User> users = new ArrayList<>();

        try {

            Connection connection = ConnectionUtil.getConnection();
            CallableStatement callableStatement = connection.prepareCall(sql);
            ResultSet rs = callableStatement.executeQuery();
            User user;

            while (rs.next()) {
                user = new User();

                user.setId(rs.getInt("id"));
                user.setFullName(rs.getString("full_name"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setActive(rs.getBoolean("active"));
                user.setBalance(rs.getDouble("balance"));
                user.setChatId(rs.getString("chat_id"));

                users.add(user);
            }
            return users;


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    @Override
    public Result addAdmin(String phoneNumber) {


        boolean correct = Pattern.matches("^(\\+998)?\\d{9}$", phoneNumber);

        if (!correct) {
            return new Result("Telefon formati noto'g'ri", false);
        } else {
            if (!phoneNumber.startsWith("+998")) {
                phoneNumber = "+998" + phoneNumber;
            }

            String query = "{call add_Admin(?,?,?) }";

            try {

                Connection connection = ConnectionUtil.getConnection();
                CallableStatement callableStatement = connection.prepareCall(query);
                callableStatement.setString(1, phoneNumber);
                callableStatement.registerOutParameter(2, Types.VARCHAR);
                callableStatement.registerOutParameter(3, Types.BOOLEAN);

                callableStatement.execute();

                return new Result(callableStatement.getString(2), callableStatement.getBoolean(3));
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    public Result confirmAd(Integer productId) {
        UserServiceImpl us = new UserServiceImpl();
        Product product = us.getProduct(productId);

        if (productId == null || productId < 1) {
            return new Result("id xato", false);
        }
        if (product == null) {
            return new Result("Bunday product mavjud emas", false);
        }

        if (product.getActive())
            return new Result("Maxsulot o'zi faol xolatda", false);

        String query = "{ call confirm_ad(?,?,?) }";
        try {

            Connection connection = ConnectionUtil.getConnection();
            CallableStatement callableStatement = connection.prepareCall(query);
            callableStatement.setInt(1, productId);
            callableStatement.registerOutParameter(2, Types.VARCHAR);
            callableStatement.registerOutParameter(3, Types.BOOLEAN);

            callableStatement.execute();
            return new Result(callableStatement.getString(2), callableStatement.getBoolean(3));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Result addCategory(Category category) {
        String query = "{call add_category(?,?,?) }";

        Gson gson = new Gson();

        try {

            Connection connection = ConnectionUtil.getConnection();
            CallableStatement callableStatement = connection.prepareCall(query);
            if (category == null) {
                return new Result("Kategoriya mavjud emas", false);
            }

            category.setName(category.getName().trim());
            if (category.getName().isEmpty()) {
                return new Result("Katgeriya nomi bo'sh bo'lishi mumkin emas", false);
            }

            callableStatement.setString(1, gson.toJson(category));
            callableStatement.registerOutParameter(2, Types.VARCHAR);
            callableStatement.registerOutParameter(3, Types.BOOLEAN);

            callableStatement.execute();

            return new Result(callableStatement.getString(2),
                    callableStatement.getBoolean(3));
        } catch (SQLException e) {
            e.printStackTrace();
            return new Result("Bunday kategoriya mavjud. Yoki nimadir xato ketdi", false);
        }
    }

    @Override
    public Result editCategoryName(Integer categoryId, String newName) {
        String query = "{call edit_Name(?,?,?,?) }";

        try {

            Connection connection = ConnectionUtil.getConnection();
            CallableStatement callableStatement = connection.prepareCall(query);
            if (categoryId == null || categoryId < 1) {
                return new Result("Kategoriya yo'q", false);
            }

            newName = newName.trim();
            if (newName.isEmpty()) {
                return new Result("Kategoriya nomi bo'sh", false);
            }

            callableStatement.setInt(1, categoryId);
            callableStatement.setString(2, newName);
            callableStatement.registerOutParameter(3, Types.VARCHAR);
            callableStatement.registerOutParameter(4, Types.BOOLEAN);
            callableStatement.execute();

            return new Result(callableStatement.getString(3), callableStatement.getBoolean(4));
        } catch (SQLException e) {
            return new Result("Nimadir xato", false);
        }
    }

    @Override
    public Result deleteCategory(Integer categoryId) {

        if (categoryId == null || categoryId < 1) {
            return new Result("Nimadir xato", false);
        }

        String sql = "{call delete_category(?, ?, ?)}";

        try {

            Connection connection = ConnectionUtil.getConnection();
            CallableStatement callableStatement = connection.prepareCall(sql);

            callableStatement.setInt(1, categoryId);
            callableStatement.registerOutParameter(2, Types.VARCHAR);
            callableStatement.registerOutParameter(3, Types.BOOLEAN);

            callableStatement.execute();

            return new Result(callableStatement.getString(2),
                    callableStatement.getBoolean(3));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new Result("Nimadir xato", false);
    }

    public Result deleteProduct(Integer productId) {
        if (productId == null || productId < 1) {
            return new Result("Nimadir xato", false);
        }

        String sql = "{call delete_product(?, ?, ?)}";

        try {

            Connection connection = ConnectionUtil.getConnection();
            CallableStatement callableStatement = connection.prepareCall(sql);

            callableStatement.setInt(1, productId);
            callableStatement.registerOutParameter(2, Types.VARCHAR);
            callableStatement.registerOutParameter(3, Types.BOOLEAN);

            callableStatement.execute();

            return new Result(callableStatement.getString(2),
                    callableStatement.getBoolean(3));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new Result("Nimadir xato", false);
    }

    @Override
    public Result blockUser(Integer userId) {
        if (userId == null || userId < 1) {
            return new Result("Nimadir xato", false);
        }

        String sql = "{call block_User(?, ?, ?)}";

        try {

            Connection connection = ConnectionUtil.getConnection();
            CallableStatement callableStatement = connection.prepareCall(sql);
            callableStatement.setInt(1, userId);
            callableStatement.registerOutParameter(2, Types.VARCHAR);
            callableStatement.registerOutParameter(3, Types.BOOLEAN);

            callableStatement.execute();

            return new Result(callableStatement.getString(2),
                    callableStatement.getBoolean(3));

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            new Result("Nimadir xato", false);
        }

        return new Result("Nimadir xato", false);
    }

    @Override
    public Category getCategoryById(Integer id) {
        return ComponentContainer.categories.stream()
                .filter(category -> category.getId().equals(id))
                .findFirst()
                .orElse(null);
    }


    @Override
    public Result sendMessage(String message, String chatId) {

        if (message.trim().length() == 0) return new Result("Text yozish majburiy!", false);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);


        for (User user : users) {
            UserServiceImpl userService = new UserServiceImpl();
            if (userService.getUserRole(user.getChatId()) == null) {
                new MyThread(user, sendMessage).start();
            }
        }


        ComponentContainer.adminStatus.remove(chatId);
        return new Result("Xabar muvaffiqiyatli jo'natildi", true);
    }

    @Override
    public Result sendMessage(SendPhoto sendPhoto, String chatId) {

        for (User user : users) {
            if (userService.getUserRole(user.getChatId()) == null) {
                new MyThread(user, sendPhoto).start();

            }
        }

        ComponentContainer.adminStatus.remove(chatId);
        return new Result("Xabar muvaffiqiyatli jo'natildi", true);
    }

    public Result sendMessage(SendVideo sendVideo, String chatId) {

        for (User user : users) {
            if (userService.getUserRole(user.getChatId()) == null) {
                new MyThread(user, sendVideo).start();

            }
        }

        ComponentContainer.adminStatus.remove(chatId);
        return new Result("Xabar muvaffiqiyatli jo'natildi", true);
    }

    public Result sendMessage(SendAudio sendAudio, String chatId) {

        for (User user : users) {
            if (userService.getUserRole(user.getChatId()) == null) {
                new MyThread(user, sendAudio).start();

            }
        }

        ComponentContainer.adminStatus.remove(chatId);
        return new Result("Xabar muvaffiqiyatli jo'natildi", true);
    }

    public Result sendMessage(SendPoll sendPoll, String chatId) {

        for (User user : users) {
            if (userService.getUserRole(user.getChatId()) == null) {
                sendPoll.setChatId(user.getChatId());
                ComponentContainer.MY_BOT.sendMsg(sendPoll);
            }
        }

        ComponentContainer.adminStatus.remove(chatId);
        return new Result("Xabar muvaffiqiyatli jo'natildi", true);
    }
    public Result sendMessage(SendVoice sendVoice, String chatId) {

        for (User user : users) {
            if (userService.getUserRole(user.getChatId()) == null) {
                sendVoice.setChatId(user.getChatId());
                ComponentContainer.MY_BOT.sendMsg(sendVoice);
            }
        }

        ComponentContainer.adminStatus.remove(chatId);
        return new Result("Xabar muvaffiqiyatli jo'natildi", true);
    }
    public Result sendMessage(SendAnimation sendAnimation, String chatId) {

        for (User user : users) {
            if (userService.getUserRole(user.getChatId()) == null) {
                sendAnimation.setChatId(user.getChatId());
                ComponentContainer.MY_BOT.sendMsg(sendAnimation);
            }
        }

        ComponentContainer.adminStatus.remove(chatId);
        return new Result("Xabar muvaffiqiyatli jo'natildi", true);
    }
}

class MyThread extends Thread {
    User user;
    SendPhoto sendPhoto;
    SendMessage sendMessage;
    SendVideo sendVideo;
    SendAudio sendAudio;

    public MyThread(User user, SendPhoto sendPhoto) {
        this.user = user;
        this.sendPhoto = sendPhoto;
    }

    public MyThread(User user, SendMessage sendMessage) {
        this.user = user;
        this.sendMessage = sendMessage;
    }

    public MyThread(User user, SendVideo sendVideo) {
        this.user = user;
        this.sendVideo = sendVideo;
    }

    public MyThread(User user, SendAudio sendAudio) {
        this.user = user;
        this.sendAudio = sendAudio;
    }

    @Override
    public void run() {
        if (sendPhoto != null) {
            sendPhoto.setChatId(user.getChatId());
            ComponentContainer.MY_BOT.sendMsg(sendPhoto);
        } else if (sendVideo != null) {
            sendVideo.setChatId(user.getChatId());
            ComponentContainer.MY_BOT.sendMsg(sendVideo);
        } else if (sendAudio != null) {
            sendAudio.setChatId(user.getChatId());
            ComponentContainer.MY_BOT.sendMsg(sendAudio);
        } else {
            sendMessage.setChatId(user.getChatId());
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
        }
    }

}
