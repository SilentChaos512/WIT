package net.silentchaos512.wit.config;

import net.minecraftforge.common.config.Configuration;

public class ConfigOption {

  String name;
  String category;
  String comment = "";

  public ConfigOption(String name, String category) {

    this.name = name;
    this.category = category;
  }

  public ConfigOption loadValue(Configuration c) {

    return this;
  }

  public ConfigOption setName(String value) {

    name = value;
    return this;
  }

  public ConfigOption setCategory(String value) {

    category = value;
    return this;
  }

  public ConfigOption setComment(String value) {

    comment = value;
    return this;
  }
}
