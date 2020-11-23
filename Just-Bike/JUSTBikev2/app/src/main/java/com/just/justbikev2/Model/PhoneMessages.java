package com.just.justbikev2.Model;

public class PhoneMessages {
  String time ,message,body;

  public PhoneMessages() {
  }

  public PhoneMessages(String time, String message, String body) {
    this.time = time;
    this.message = message;
    this.body = body;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
