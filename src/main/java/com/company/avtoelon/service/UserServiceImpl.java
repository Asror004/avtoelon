package com.company.avtoelon.service;

import com.company.avtoelon.container.ComponentContainer;
import com.company.avtoelon.entity.*;
import com.company.avtoelon.enums.Region;
import com.company.avtoelon.payload.Result;
import com.company.avtoelon.util.ConnectionUtil;
import com.google.gson.Gson;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserServiceImpl implements UserService {

    Connection connection = ConnectionUtil.getConnection();

    @Override
    public Result addUser(User user) {
        String sql = "{call add_user(?, ?, ?) }";

        try {

            Connection connection = ConnectionUtil.getConnection();
            CallableStatement callableStatement = connection.prepareCall(sql);

            Gson gson = new Gson();
            String userInfo = gson.toJson(user);

            Class.forName("org.postgresql.Driver");

            callableStatement.setString(1, userInfo);
            callableStatement.registerOutParameter(2, Types.VARCHAR);
            callableStatement.registerOutParameter(3, Types.BOOLEAN);

            callableStatement.execute();

            String message = callableStatement.getString(2);
            boolean success = callableStatement.getBoolean(3);

            return new Result(message, success);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Result addProduct(Product product, Description description) {


        String sqlDescription = "{call add_description(?,?,?,?)}";
        String sqlProduct = "{call add_product(?, ?, ?) }";

        try {

            Connection connection = ConnectionUtil.getConnection();
            CallableStatement callableStatementForDesc = connection.prepareCall(sqlDescription);
            CallableStatement callableStatementForPro = connection.prepareCall(sqlProduct);

            Gson gson = new Gson();
            String descriptionInfo = gson.toJson(description);

//            Class.forName("org.postgresql.Driver");

            callableStatementForDesc.setString(1, descriptionInfo);
            callableStatementForDesc.registerOutParameter(2, Types.VARCHAR);
            callableStatementForDesc.registerOutParameter(3, Types.INTEGER);
            callableStatementForDesc.registerOutParameter(4, Types.BOOLEAN);

            callableStatementForDesc.execute();
            boolean aBoolean = callableStatementForDesc.getBoolean(4);

            if (!aBoolean) {
                return new Result(callableStatementForDesc.getString(2), aBoolean);
            }

            int descriptionId = callableStatementForDesc.getInt(3);
            product.setDescriptionId(descriptionId);

            String productInfo = gson.toJson(product);


            callableStatementForPro.setString(1, productInfo);
            callableStatementForPro.registerOutParameter(2, Types.VARCHAR);
            callableStatementForPro.registerOutParameter(3, Types.BOOLEAN);

            callableStatementForPro.execute();

            return new Result(callableStatementForPro.getString(2),
                    callableStatementForPro.getBoolean(3));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Result addFavorite(Integer productId, Integer userId) {
        String sql = "{ call add_favorite(?, ?, ?, ? ) }";

        try {


            Connection connection = ConnectionUtil.getConnection();
            CallableStatement callableStatement = connection.prepareCall(sql);

            callableStatement.setInt(1, productId);
            callableStatement.setInt(2, userId);
            callableStatement.registerOutParameter(3, Types.VARCHAR);
            callableStatement.registerOutParameter(4, Types.BOOLEAN);

            callableStatement.execute();

            return new Result(callableStatement.getString(3),
                    callableStatement.getBoolean(4));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Result removeFavorite(Integer productId, Integer userId) {
        String sql = "{call remove_favorite(?, ?, ?, ?) }";

        try {

            Connection connection = ConnectionUtil.getConnection();
            CallableStatement callableStatement = connection.prepareCall(sql);
            callableStatement.setInt(1, productId);
            callableStatement.setInt(2, userId);
            callableStatement.registerOutParameter(3, Types.VARCHAR);
            callableStatement.registerOutParameter(4, Types.BOOLEAN);

            callableStatement.execute();

            return new Result(callableStatement.getString(3),
                    callableStatement.getBoolean(4));
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    public List<Category> getCategories(Integer parentId) {
        if (parentId != null && parentId < 1) {
            return null;
        }

        String sql = "{call get_categories(?)}";
        List<Category> categories = new ArrayList<>();

        try {
            Connection connection = ConnectionUtil.getConnection();
            CallableStatement callableStatement = connection.prepareCall(sql);

            callableStatement.setObject(1, parentId);

            ResultSet rs = callableStatement.executeQuery();
            Category category = null;
            while (rs.next()) {
                category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                if (rs.getInt("parent_id") == 0)
                    category.setParentId(null);
                else
                    category.setParentId(rs.getInt("parent_id"));

                categories.add(category);
            }
            return categories;


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    @Override
    public List<Product> getProductsList(Integer categoryId, String rol) {
        if (categoryId != null && categoryId < 1) {
            return null;
        }

        String sql = "{call get_products(?, ?)}";
        List<Product> products = new ArrayList<>();

        try {

            Connection connection = ConnectionUtil.getConnection();
            CallableStatement callableStatement = connection.prepareCall(sql);

            callableStatement.setObject(1, categoryId);
            callableStatement.setString(2, rol);

            ResultSet rs = callableStatement.executeQuery();

            Product product;
            while (rs.next()) {
                product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getDouble("price"));
                product.setPhotoId(rs.getString("photo_id"));
                product.setCreatedAt(rs.getString("created_at"));
                product.setActive(rs.getBoolean("active"));
                product.setUserId(rs.getInt("user_id"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setNegotiable(rs.getBoolean("negotiable"));
                product.setDescriptionId(rs.getInt("description_id"));
                product.setRegion(Region.valueOf(rs.getString("region")));

                products.add(product);
            }
            return products;


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    @Override
    public List<Product> getFavorites(Integer userId) {
        if (userId == null || userId < 1) {
            return null;
        }

        String sql = "{call get_favorites(?)}";
        List<Product> products = new ArrayList<>();

        try {

            Connection connection = ConnectionUtil.getConnection();
            CallableStatement callableStatement = connection.prepareCall(sql);
            callableStatement.setObject(1, userId);

            ResultSet rs = callableStatement.executeQuery();
            Product product;
            while (rs.next()) {
                product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getDouble("price"));
                product.setPhotoId(rs.getString("photo_id"));
                product.setCreatedAt(rs.getString("created_at"));
                product.setActive(rs.getBoolean("active"));
                product.setUserId(rs.getInt("user_id"));
                product.setCategoryId(rs.getInt("category_id"));
                product.setNegotiable(rs.getBoolean("negotiable"));
                product.setDescriptionId(rs.getInt("description_id"));
                product.setRegion(Region.valueOf(rs.getString("region")));

                products.add(product);
            }
            return products;


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    private boolean checkId(Integer id) {
        return id == null || id < 1;
    }

    private Product makeProduct(ResultSet rs) {
        Product product = new Product();

        try {
            product.setId(rs.getInt("id"));
            product.setName(rs.getString("name"));
            product.setPrice(rs.getDouble("price"));
            product.setPhotoId(rs.getString("photo_id"));
            product.setCreatedAt(rs.getString("created_at"));
            product.setActive(rs.getBoolean("active"));
            product.setUserId(rs.getInt("user_id"));
            product.setCategoryId(rs.getInt("category_id"));
            product.setNegotiable(rs.getBoolean("negotiable"));
            product.setDescriptionId(rs.getInt("description_id"));
            product.setRegion(Region.valueOf(rs.getString("region")));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return product;
    }

    @Override
    public Product getProduct(Integer productId) {

        if (checkId(productId)) {
            return null;
        }

        String sql = "{call get_product(?)}";
        Product product = new Product();

        try {

            Connection connection = ConnectionUtil.getConnection();
            CallableStatement callableStatement = connection.prepareCall(sql);
            callableStatement.setObject(1, productId);

            ResultSet rs = callableStatement.executeQuery();

            if (rs.next()) {
                product = makeProduct(rs);
            } else {
                return null;
            }

            return product;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return product;
    }

    @Override
    public User getUserByChatId(String chatId) {

        String sql = "{call get_user_by_chat_id(?)}";

        try {

            Connection connection = ConnectionUtil.getConnection();
            CallableStatement callableStatement = connection.prepareCall(sql);
            callableStatement.setString(1, chatId);

            ResultSet rs = callableStatement.executeQuery();

            User user = new User();
            while (rs.next()) {
                user.setId(rs.getInt("id"));
                user.setFullName(rs.getString("full_name"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setActive(rs.getBoolean("active"));
                user.setBalance(rs.getDouble("balance"));
                user.setChatId(rs.getString("chat_id"));
            }

            return user;


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Result setBalance(String chatId, Double newPrice) {
        String sql = "{call set_balance(?, ?, ?, ?)}";

        try {

            Connection connection = ConnectionUtil.getConnection();
            CallableStatement callableStatement = connection.prepareCall(sql);
            User user = getUserByChatId(chatId);

            callableStatement.setInt(1, user.getId());
            callableStatement.setDouble(2, newPrice);
            callableStatement.registerOutParameter(3, Types.VARCHAR);
            callableStatement.registerOutParameter(4, Types.BOOLEAN);

            callableStatement.execute();

            return new Result(callableStatement.getString(3),
                    callableStatement.getBoolean(4));


        } catch (SQLException e) {
            return new Result("Nimadir xato", false);
        }
    }


    public List<Category> getAllCategories() {

        String sql = "select * from get_categories_m_view";

        try {
            PreparedStatement preparedStatement = ConnectionUtil.getConnection().prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Category> categories = new ArrayList<>();

            while (resultSet.next()) {

                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int parentId = resultSet.getInt("parent_id");

                Category category = new Category(id, name, parentId);
                categories.add(category);

            }

            return categories;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

    public Category getCategory(Integer categoryId) {
        if (checkId(categoryId)) {
            return null;
        }

        String sql = "{call get_category(?)}";
        Category category = new Category();

        try  {

            Connection connection = ConnectionUtil.getConnection();
            CallableStatement callableStatement = connection.prepareCall(sql);
            callableStatement.setObject(1, categoryId);

            ResultSet rs = callableStatement.executeQuery();

            if (rs.next()) {
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                category.setParentId(rs.getInt("parent_id"));
            } else {
                return null;
            }

            return category;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return category;
    }


    public void getProductDescription() {

        ComponentContainer.productDescriptions = new ArrayList<>();

        String sql = "{call get_product_description()}";

        try  {

            CallableStatement callableStatement = ConnectionUtil.getConnection().prepareCall(sql);
            ResultSet resultSet = callableStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                String photo_id = resultSet.getString("photo_id");
                Date created_at = resultSet.getDate("created_at");
                boolean active = resultSet.getBoolean("active");
                Integer user_id = resultSet.getInt("user_id");
                int category_id = resultSet.getInt("category_id");
                boolean negotiable = resultSet.getBoolean("negotiable");
                Integer description_id = resultSet.getInt("description_id");
                String region = resultSet.getString("region_name");
                Integer d_id = resultSet.getInt("d_id");
                String info_ = resultSet.getString("info_");
                String phone_number = resultSet.getString("phone_number");
                long views = resultSet.getLong("views");


                ProductDescription productDescription = new ProductDescription(id, name, price, photo_id, created_at
                        , active, user_id, category_id,
                        negotiable, description_id, region, d_id, info_, phone_number, views);

                ComponentContainer.productDescriptions.add(productDescription);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Description getDescription(Integer productId){
        if (productId==null || productId<1){
            return null;
        }

        String sql = "{call get_description(?) }";
        try (
                CallableStatement callableStatement = connection.prepareCall(sql)
        ) {

            callableStatement.setInt(1, productId);

            ResultSet rs = callableStatement.executeQuery();

            Description description = new Description();
            while (rs.next()){
                description.setId(rs.getInt("id"));
                description.setText(rs.getString("info"));
                description.setPhoneNumber(rs.getString("phone_number"));
                description.setNumberOfView(rs.getLong("views"));
            }

            return description;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Product> getMyProducts(Integer userId){
        if (userId==null || userId<1){
            return null;
        }

        List<Product> products = new ArrayList<>();
        String sql = "{call get_my_products(?)}";
        try (
                CallableStatement callableStatement = connection.prepareCall(sql)
        ) {

            callableStatement.setInt(1, userId);

            ResultSet rs = callableStatement.executeQuery();

            Product product;
            while (rs.next()){
                product = makeProduct(rs);

                products.add(product);
            }

            return products;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    @Override
    public String getUserRole(String chatId) {
        if (chatId.trim().isEmpty()) {
            return null;
        }

        String sql = "{call get_role_by_chat_id(?, ?)}";

        try (
                CallableStatement callableStatement = connection.prepareCall(sql)) {

            callableStatement.registerOutParameter(2, Types.VARCHAR);
            callableStatement.setString(1, chatId);

            callableStatement.execute();

            return callableStatement.getString(2);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
