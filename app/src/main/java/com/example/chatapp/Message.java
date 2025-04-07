package com.example.chatapp;

public class Message {
    public static String SENT_BY_ME = "me";
    public static String SENT_BY_BOT="bot";
    private String message;
    private String sent_by;
    private String time;
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Message(String message, String time, String sent_by) {
        this.message = message;
        this.time = time;
        this.sent_by = sent_by;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Message(String message, String sent_by) {
        this.message = message;
        this.sent_by = sent_by;
    }

    public String getSent_by() {
        return sent_by;
    }

    public void setSent_by(String sent_by) {
        this.sent_by = sent_by;
    }
}
