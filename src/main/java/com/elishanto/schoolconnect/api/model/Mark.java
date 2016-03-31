package com.elishanto.schoolconnect.api.model;

public class Mark {
    private String mark;
    private String desc;

    public Mark(String mark, String desc) {
        this.mark = mark;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public String getMark() {
        return mark;
    }
}
