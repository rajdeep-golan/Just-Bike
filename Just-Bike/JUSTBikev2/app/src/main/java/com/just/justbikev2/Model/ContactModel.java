package com.just.justbikev2.Model;

import java.util.HashMap;
import java.util.Map;

public class ContactModel {
  Map<String, String> namePhoneMap ;

  public ContactModel(Map<String, String> namePhoneMap) {
    this.namePhoneMap = namePhoneMap;
  }

  public ContactModel() {
  }

  public Map<String, String> getNamePhoneMap() {
    return namePhoneMap;
  }

  public void setNamePhoneMap(Map<String, String> namePhoneMap) {
    this.namePhoneMap = namePhoneMap;
  }
}

