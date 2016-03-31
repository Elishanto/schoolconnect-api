package com.elishanto.schoolconnect.api.controller;

import com.elishanto.schoolconnect.api.model.Mark;
import com.elishanto.schoolconnect.api.model.User;
import com.elishanto.schoolconnect.api.util.Cookies;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
public class MarksController {

    private static Logger logger = Logger.getLogger(MarksController.class);

    @RequestMapping("/marks")
    public Map marks(@RequestParam(value = "login") String login, @RequestParam(value = "password") String password) throws IOException {
        User user = new User(login, password);
        Map<String, String> cookies = new Cookies().getCookies(user);
        SortedMap<String, List<Mark>> map = new TreeMap<>();
        if(cookies.get(".ASPXAUTH") == null) {
            logger.error("\"" + user.getLogin() + "\" - \"" + user.getPassword() + "\"");
            Map<String, String> res = new HashMap<>();
            res.put("Error", "Invalid credentials");
            return res;
        } else {
            Connection marksConn = Jsoup.connect("https://www.schoolconnect.ru/child/childmarks.aspx").cookies(cookies);
            Document childmarks = marksConn.timeout(10 * 1000).get();
            for (Element row : childmarks.select("#freezeOne_STR").select("tr[id^=freezeOne_Row]")) {
                ArrayList<Mark> elements = new ArrayList<>();
                ArrayList<String> doubles = new ArrayList<>();
                for (Element cell : row.select("td[id^=freezeOne_Cell]")) {
                    String text = cell.text();
                    String desc = cell.attr("title");
                    if(desc.contains(":"))
                        desc = desc.split(":")[1].split("\n")[0].trim();
                    if(cell.getElementsByClass("frcell").first() != null) {
                        doubles.add(text);
                    } else if (text.contains("/") || text.matches("^[0-9]{1,3}$")) {
                        if(cell.toString().contains("bold"))
                            text = "B" + text;
                        elements.add(new Mark(text, desc));
                    }
                }
                elements.add(0, new Mark(doubles.get(0), "avg"));
                if(elements.size() > 1)
                    map.put(childmarks.select("#freezeOne_STL").select("tr[id^=freezeOne_Row" + row.id().replaceAll("\\D+", "") + "]").select("td").attr("title").split(",")[0], elements);
            }
            logger.info("Client with LOGIN=\"" + user.getLogin() + "\" and password=\"" + user.getPassword() + "\"");
            return map;
        }
    }
}