package com.elishanto.schoolconnect.api.util;

import com.elishanto.schoolconnect.api.model.User;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

public class Cookies {
    public Map<String, String> getCookies(User user) throws IOException {
        Connection connection = Jsoup.connect("https://www.schoolconnect.ru/login.aspx");
        Document document = connection.get();
        String viewstate = document.select("#__VIEWSTATE").attr("value");
        connection.data("__VIEWSTATE", viewstate);
        connection.data("LogintText", user.getLogin());
        connection.data("PasswordText", user.getPassword());
        connection.data("LoginButton", "");
        connection.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36");
        Connection.Response result = connection.timeout(10*10000).execute();
        return result.cookies();
    }
}