package com.elishanto.schoolconnect.api.util;

import com.elishanto.schoolconnect.api.model.User;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Map;

public class New {
    public boolean haveNew(String login, String password) throws IOException {
        User user = new User(login, password);
        Map<String, String> cookies = new Cookies().getCookies(user);
        if(cookies.get(".ASPXAUTH") == null) {
            return false;
        } else {
            Connection marksConn = Jsoup.connect("https://www.schoolconnect.ru/child/childmarks.aspx").cookies(cookies);
            Document childmarks = marksConn.timeout(10 * 1000).get();
            for (Element row : childmarks.select("#freezeOne_STR").select("tr[id^=freezeOne_Row]")) {
                for (Element cell : row.select("td[id^=freezeOne_Cell]")) {
                    String text = cell.text();
                    if ((text.contains("/") || text.matches("^[0-9]{1,3}$")) && cell.toString().contains("bold")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
