package net.silentchaos512.wit.lib;

import net.minecraft.client.resources.I18n;

public class LocalizationHelper {

  String prefix = "wit:";

  public static LocalizationHelper instance;

  public static void init() {

    instance = new LocalizationHelper();
  }

  public String get(String key, Object... params) {

    return I18n.format(prefix + key, params);
  }
}
