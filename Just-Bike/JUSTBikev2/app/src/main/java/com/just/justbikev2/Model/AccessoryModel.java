package com.just.justbikev2.Model;

public class AccessoryModel {
  private boolean selected;
    private String name;
    private String image,cost;

    public AccessoryModel() {
    }

    public AccessoryModel(String name, String image, String cost) {
        this.name = name;
        this.image = image;
        this.cost = cost;
    }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
}
