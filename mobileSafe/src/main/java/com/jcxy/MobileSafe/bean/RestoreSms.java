package com.jcxy.MobileSafe.bean;

/**
 * Created by hp on 2016/10/4.
 */

public class RestoreSms {
    private String address;
    private String type;
    private String body;
    private String date;

    @Override
    public String toString() {
        return "RestoreSms{" +
                "address='" + address + '\'' +
                ", type='" + type + '\'' +
                ", body='" + body + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
