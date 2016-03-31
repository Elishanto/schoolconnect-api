package com.elishanto.schoolconnect.api.controller;

import com.elishanto.schoolconnect.api.model.User;
import com.elishanto.schoolconnect.api.util.Cookies;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    private Logger logger = Logger.getLogger(AuthController.class);

    @RequestMapping("/auth")
    public Map<String, Object> auth(@RequestParam(value = "login") String login, @RequestParam(value = "password") String password) throws IOException {
        User user = new User(login, password);
        Map<String, String> cookies = new Cookies().getCookies(user);
        Map<String, Object> res = new HashMap<>();
        res.put("success", Jsoup.connect("https://www.schoolconnect.ru/child/childmarks.aspx").cookies(cookies).get().getElementsByClass("logout").first() != null);
        if ((boolean) res.get("success"))
            logger.info(String.format("Authentication %s:%s", user.getLogin(), user.getPassword()));
        else {
            logger.error(String.format("Authentication %s:%s", user.getLogin(), user.getPassword()));
            return res;
        }
        res.put("email", Jsoup.connect("https://www.schoolconnect.ru/user/profile.aspx").cookies(cookies).get().getElementsByAttributeValue("name", "ctl00$body$EMailTextBox").first().attr("value"));
        return res;
    }
}