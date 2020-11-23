package com.just.justbikev2.Model;

public class Feedback {
  String image;
  String feedback;

  public Feedback(String image, String feedback) {
    this.image = image;
    this.feedback = feedback;
  }

  public Feedback() {
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getFeedback() {
    return feedback;
  }

  public void setFeedback(String feedback) {
    this.feedback = feedback;
  }
}
