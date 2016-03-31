package com.elishanto.schoolconnect.api.controller;

import com.elishanto.schoolconnect.api.model.Mark;
import com.elishanto.schoolconnect.api.model.Task;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class HomeworkController {
    private static Logger logger = Logger.getLogger(HomeworkController.class);

    Map<String, String> data;
    Map<String, String> cookies;

    @RequestMapping("/homework")
    public Object homework(@RequestParam(value = "login") String login, @RequestParam(value = "password") String password, @RequestParam(value = "date", required = false) String date) throws ParseException, IOException {
        User user = new User(login, password);
        cookies = new Cookies().getCookies(user);
        if(cookies.get(".ASPXAUTH") == null) {
            logger.error("\"" + user.getLogin() + "\" - \"" + user.getPassword() + "\"");
            Map<String, String> res = new HashMap<>();
            res.put("Error", "Invalid credentials");
            return res;
        } else {
            List<Task> tasks;
            Connection conn = Jsoup.connect("https://www.schoolconnect.ru/child/childhomework.aspx").cookies(cookies);
            data = new HashMap<>();
            Document doc = conn.timeout(10 * 1000).get();
            data.put("__VIEWSTATE", doc.select("#__VIEWSTATE").attr("value"));
            data.put("__EVENTVALIDATION", doc.select("#__EVENTVALIDATION").attr("value"));
            data.put("ctl00$body$HomeworkShow$grid$CallbackState", doc.select("#ctl00_body_HomeworkShow_grid_CallbackState").attr("value"));
            if(date == null) {
                tasks = getTasks(doc);
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new SimpleDateFormat("dd.MM.yyyy").parse(date));
                calendar.add(Calendar.HOUR_OF_DAY, 3);
                data.put("ctl00_body_HomeworkShow_FromDate_Raw", String.valueOf(calendar.getTimeInMillis()));
                data.put("ctl00$body$HomeworkShow$FromDate", date);
                conn.data(data);
                tasks = getTasks(conn.post());
            }
            return tasks;
        }
    }

    private List<Task> getTasks(Document document) throws IOException {
        List<Task> res = new ArrayList<>();
        data.put("__CALLBACKID","ctl00$body$HomeworkShow$HomeworkDetailPanel");
        for(Element row : document.getElementById("ctl00_body_HomeworkShow_grid_DXMainTable").child(0).select("tr[id^=ctl00_body_HomeworkShow_grid_DXDataRow]")) {
            List<Element> cells = row.getElementsByClass("dxgv");
            cells = cells.subList(1, cells.size());
            Task.Builder task = Task.newBuilder();
            task.setCreated(cells.get(0).text().trim());
            task.setDelivery(cells.get(1).text().trim());
            task.setSubject(cells.get(2).text().trim());
            task.setDesc(cells.get(3).text().trim());
            int status = 0;
            switch(row.select("td[id^=ctl00_body_HomeworkShow_grid_tccell]").get(1).child(0).attr("title")) {
                case "Не отмечено":
                    status = 0;
                    break;
                case "Не смог выполнить":
                    status = 1;
                    break;
                case "Выполнил тяжело":
                    status = 2;
                    break;
                case "Выполнил":
                    status = 3;
                    break;
                case "Выполнил легко":
                    status = 4;
                    break;
            }
            task.setStatus(status);

            int id = Integer.parseInt(row.getElementsByClass("labelOfRow").attr("id").replace("labelOf", ""));
            task.setId(id);
            data.put("currentHomeworkId", String.valueOf(id));
            data.put("__CALLBACKPARAM", "c0:" + id);
            String t = Jsoup.connect("https://www.schoolconnect.ru/child/childhomework.aspx").cookies(cookies).data(data).post().toString();
            t = t.substring(t.indexOf("<\\/script>")+"<\\/script>".length(), t.lastIndexOf("</script>")).replace("})", "");
            String full = Jsoup.parse(t).getElementById("ctl00_body_HomeworkShow_HomeworkDetailPanel_WorkText").text();
            if(full.equals("Нет полного описания задания")) {
                task.setFull(cells.get(3).text().trim());
                task.setDesc("");
            }
            else
                task.setFull(full);
            data.remove("currentHomeworkId");
            data.remove("__CALLBACKPARAM");
            res.add(task.build());
        }
        return res;
    }
}
