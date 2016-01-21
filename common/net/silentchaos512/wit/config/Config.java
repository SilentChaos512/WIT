package net.silentchaos512.wit.config;

import java.io.File;
import java.util.List;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.silentchaos512.wit.lib.EnumHudPosition;
import net.silentchaos512.wit.lib.EnumJustification;

public class Config {

  /*
   * HUD display position
   */

  public static EnumJustification hudJustification = EnumJustification.CENTER;
  public static String hudJustificationComment = "The justification of text in the HUD. Can be LEFT, CENTER, or RIGHT";

  public static EnumHudPosition hudPosition = EnumHudPosition.TOP_CENTER;
  public static String hudPositionComment = "The positioning of the HUD. Can be TOP_LEFT, TOP_CENTER, TOP_RIGHT, CENTER_LEFT, CENTER_RIGHT, BOTTOM_LEFT, BOTTOM_MIDDLE, or BOTTOM_RIGHT";

  /*
   * HUD display options
   */

  //@formatter:off
  public static ConfigOptionHudElement hudObjectName = new ConfigOptionHudElement(
      "object_name", true, false, "&f")
      .setComment("Display the block/entity name in the HUD. Example: Iron Ore, Chicken");
  public static ConfigOptionHudElement hudResourceName = new ConfigOptionHudElement(
      "resource_name", true, true, "&7")
      .setComment("Display the resource name in the HUD. Example: minecraft:stone.");
  public static ConfigOptionHudElement hudModName = new ConfigOptionHudElement(
      "mod_name", true, false, "&e")
      .setComment("Display the mod name in the HUD. Example: Minecraft");
  public static ConfigOptionHudElement hudIdMeta = new ConfigOptionHudElement(
      "id_and_meta", true, true, "&o")
      .setComment("Display the ID and metadata in the HUD. Example: [1:0]");
  public static ConfigOptionHudElement hudTileEntity = new ConfigOptionHudElement(
      "tile_entity", true, false, "&7")
      .setComment("Tells if a tile entity exists for the block being looked at. Example: Furnace (TE)");
  public static ConfigOptionHudElement hudHarvestable = new ConfigOptionHudElement(
      "harvestability", true, true, "&a", "&c")
      .setComment("Shows whether or not a block is harvestable and with what kind of tool.");
  //@formatter:on

  /*
   * Tooltip display options
   */

  public static boolean tooltipDisplayModName = true;
  public static String tooltipDisplayModNameComment = "Display the name of the mod in tooltips.";
  public static boolean tooltipDisplayModNameShift = false;
  public static String tooltipDisplayModNameShiftComment = "Display the name of the mod only when holding shift.";
  public static boolean tooltipDisplayOreDict = true;
  public static String tooltipDisplayOreDictComment = "Display the ore dictionary entries for the block/item.";
  public static boolean tooltipDisplayOreDictShift = true;
  public static String tooltipDisplayOreDictShiftComment = "Display the ore dictionary entries only when holding shift.";

  /*
   * Formatting
   */

  public static String formatModName = "&e";
  // Originally: The formatting codes to use in the tooltip. Use & to substitute for the control character.
  public static String formatModNameComment = "Formatting for mod names.";
  public static String formatResourceName = "&7";
  public static String formatResourceNameComment = "Formatting for resource names.";

  static final String splitter = Configuration.CATEGORY_SPLITTER;
  public static final String CAT_MAIN = "main";
  public static final String CAT_HUD = CAT_MAIN + splitter + "hud";
  public static final String CAT_HUD_DISPLAY = CAT_HUD + splitter + "display";
  public static final String CAT_HUD_POSITION = CAT_HUD + splitter + "positioning";
  public static final String CAT_TOOLTIP = CAT_MAIN + splitter + "tooltip";
  public static final String CAT_TOOLTIP_FORMAT = CAT_TOOLTIP + splitter + "formatting";

  private static File configFile;
  private static Configuration c;

  public static void init(File file) {

    configFile = file;
    c = new Configuration(file);
    load();
  }

  public static void load() {

    try {
      //c.load();
      String str;

      /*
       * HUD display positioning
       */

      str = c.getString("TextJustification", CAT_HUD_POSITION, "CENTER", hudJustificationComment,
          EnumJustification.getValidValues());
      for (EnumJustification j : EnumJustification.values()) {
        if (str.equals(j.name())) {
          hudJustification = j;
        }
      }

      str = c.getString("Position", CAT_HUD_POSITION, "TOP_CENTER", hudPositionComment,
          EnumHudPosition.getValidValues());
      for (EnumHudPosition p : EnumHudPosition.values()) {
        if (str.equals(p.name())) {
          hudPosition = p;
        }
      }

      /*
       * HUD display options
       */

      hudObjectName.loadValue(c);
      hudResourceName.loadValue(c);
      hudModName.loadValue(c);
      hudIdMeta.loadValue(c);
      hudTileEntity.loadValue(c);
      hudHarvestable.loadValue(c);

      /*
       * Tooltip display options
       */

      tooltipDisplayModName = c.getBoolean("ModName.Show", CAT_TOOLTIP, tooltipDisplayModName,
          tooltipDisplayModNameComment);
      tooltipDisplayModNameShift = c.getBoolean("ModName.ShiftOnly", CAT_TOOLTIP,
          tooltipDisplayModNameShift, tooltipDisplayModNameShiftComment);
      tooltipDisplayOreDict = c.getBoolean("OreDictionary.Show", CAT_TOOLTIP, tooltipDisplayOreDict,
          tooltipDisplayOreDictComment);
      tooltipDisplayOreDictShift = c.getBoolean("OreDictionary.ShiftOnly", CAT_TOOLTIP,
          tooltipDisplayOreDictShift, tooltipDisplayOreDictShiftComment);

      /*
       * Formatting
       */

      formatModName = c.getString("ModName", CAT_TOOLTIP_FORMAT, formatModName,
          formatModNameComment);
      formatResourceName = c.getString("ResourceName", CAT_TOOLTIP_FORMAT, formatResourceName,
          formatResourceNameComment);
    } catch (Exception e) {
      System.out.println("Oh noes!!! Couldn't load configuration file properly!");
    }

  }

  public static void save() {

    if (c.hasChanged()) {
      c.save();
    }
  }

  public static ConfigCategory getCategory(String str) {

    return c.getCategory(str);
  }

  public static Configuration getConfiguration() {

    return c;
  }

  public static List<IConfigElement> getConfigElements() {

    return new ConfigElement(getCategory(CAT_MAIN)).getChildElements();
  }
}
