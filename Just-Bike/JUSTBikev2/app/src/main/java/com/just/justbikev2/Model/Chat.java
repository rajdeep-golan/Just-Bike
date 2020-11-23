package com.just.justbikev2.Model;

public class Chat {
    String sender;
    String receiver;
    String message;
    String media;
    String time;

    public Chat() {
    }

    public Chat(String sender, String receiver, String message, String media, String time) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.media = media;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
