package net.silentchaos512.wit.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
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

  public static boolean hudDisplayResourceName = true;
  public static String hudDisplayResourceNameComment = "Display the resource name in the HUD. Example: minecraft:stone.";
  public static boolean hudDisplayResourceNameShift = true;
  public static String hudDisplayResourceNameShiftComment = "Display the resource name only when sneaking.";
  public static boolean hudDisplayModName = true;
  public static String hudDisplayModNameComment = "Display the name of the mod in the HUD.";
  public static boolean hudDisplayModNameShift = false;
  public static String hudDisplayModNameShiftComment = "Display the name of the mod only when sneaking.";
  public static boolean hudDisplayIdMeta = true;
  public static String hudDisplayIdMetaComment = "Display the ID and metadata of the block next to its name.";
  public static boolean hudDisplayIdMetaShift = true;
  public static String hudDisplayIdMetaShiftComment = "Display the ID and metadata only when sneaking.";

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
  public static final String CAT_HUD = "hud";
  public static final String CAT_HUD_DISPLAY = CAT_HUD + splitter + "display";
  public static final String CAT_HUD_POSITION = CAT_HUD + splitter + "positioning";
  public static final String CAT_TOOLTIP = "tooltip";
  public static final String CAT_TOOLTIP_FORMAT = CAT_TOOLTIP + splitter + "formatting";

  private static Configuration c;

  public static void init(File file) {

    c = new Configuration(file);

    try {
      c.load();
      String str;

      /*
       * HUD display positioning
       */

      str = c.getString("TextJustification", CAT_HUD_POSITION, "CENTER", hudJustificationComment);
      for (EnumJustification j : EnumJustification.values()) {
        if (str.equals(j.name())) {
          hudJustification = j;
        }
      }

      str = c.getString("Position", CAT_HUD_POSITION, "TOP_CENTER", hudPositionComment);
      for (EnumHudPosition p : EnumHudPosition.values()) {
        if (str.equals(p.name())) {
          hudPosition = p;
        }
      }

      /*
       * HUD display options
       */

      hudDisplayResourceName = c.getBoolean("ResourceName.Show", CAT_HUD_DISPLAY,
          hudDisplayResourceName, hudDisplayResourceNameComment);
      hudDisplayResourceNameShift = c.getBoolean("ResourceName.SneakingOnly", CAT_HUD_DISPLAY,
          hudDisplayResourceNameShift, hudDisplayResourceNameShiftComment);
      hudDisplayModName = c.getBoolean("ModName.Show", CAT_HUD_DISPLAY, hudDisplayModName,
          hudDisplayModNameComment);
      hudDisplayModNameShift = c.getBoolean("ModName.SneakingOnly", CAT_HUD_DISPLAY,
          hudDisplayModNameShift, hudDisplayModNameShiftComment);
      hudDisplayIdMeta = c.getBoolean("IDMeta.Show", CAT_HUD_DISPLAY, hudDisplayIdMeta,
          hudDisplayIdMetaComment);
      hudDisplayIdMetaShift = c.getBoolean("IDMeta.SneakingOnly", CAT_HUD_DISPLAY,
          hudDisplayIdMetaShift, hudDisplayIdMetaShiftComment);

      /*
       * Tooltip display options
       */

      tooltipDisplayModName = c.getBoolean("ModName.Show", CAT_TOOLTIP, tooltipDisplayModName,
          tooltipDisplayModNameComment);
      tooltipDisplayModNameShift = c.getBoolean("ModName.ShiftOnly", CAT_TOOLTIP,
          tooltipDisplayModNameShift, tooltipDisplayModNameShiftComment);
      tooltipDisplayOreDict = c.getBoolean("OreDictionary.Show", CAT_TOOLTIP, tooltipDisplayOreDict,
          tooltipDisplayOreDictComment);
      tooltipDisplayOreDictShift = c.getBoolean("OreDictionary.ShiftOnly", CAT_TOOLTIP, tooltipDisplayOreDictShift,
          tooltipDisplayOreDictShiftComment);

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
}
