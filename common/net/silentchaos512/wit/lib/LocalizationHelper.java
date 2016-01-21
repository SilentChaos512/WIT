package net.silentchaos512.wit.lib;

import net.minecraft.util.StatCollector;

public class LocalizationHelper {

  String prefix = "wit:";

  public static LocalizationHelper instance;

  public static void init() {

    instance = new LocalizationHelper();
  }

  public String get(String key) {

    return StatCollector.translateToLocal(prefix + key);
  }
}
