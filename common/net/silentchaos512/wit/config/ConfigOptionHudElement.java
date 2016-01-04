package net.silentchaos512.wit.config;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;

public class ConfigOptionHudElement extends ConfigOption {

  public static final String CATEGORY_HUD_DISPLAY = "hud" + Configuration.CATEGORY_SPLITTER
      + "display";

  boolean show = true;
  boolean sneakOnly = false;
  String format;

  public ConfigOptionHudElement(String name, boolean show, boolean sneakOnly, String format) {

    super(name, CATEGORY_HUD_DISPLAY + Configuration.CATEGORY_SPLITTER + name);
    this.show = show;
    this.sneakOnly = sneakOnly;
    this.format = format;
  }

  @Override
  public ConfigOption loadValue(Configuration c) {

    c.setCategoryComment(category, comment);
    show = c.get(category, "ShowElement", show).getBoolean();
    sneakOnly = c.get(category, "WhenSneakingOnly", sneakOnly).getBoolean();
    format = c.get(category, "FormatString", format).getString();
    return this;
  }

  public boolean shouldDisplay(EntityPlayer player) {

    if (show) {
      if (!sneakOnly || (sneakOnly && player.isSneaking())) {
        return true;
      }
    }

    return false;
  }

  public String formatString(String str) {

    return format.replaceAll("&", "\u00a7") + str;
  }

  public String getFormat() {

    return format;
  }

  public void setFormat(String value) {

    format = value;
  }

  public ConfigOptionHudElement setComment(String value) {

    super.setComment(value);
    return this;
  }
}
