package com.company.avtoelon.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtil {

    public static final String url = "jdbc:postgresql://ec2-54-75-26-218.eu-west-1.compute.amazonaws.com:5432/d6pa9eth0i971i";
    public static final String password = "9db6053a30d39ff5f16b9c75f4cf64c59986179a23bfd9f7891d345b1d7105f1";
    public static final String user = "rzlhwrewgikbtb";


    public static Connection CONNECTION;

    static {
        try {
            CONNECTION = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection(){
        return CONNECTION;
    }
}
