package net.silentchaos512.wit.config;

import net.silentchaos512.wit.lib.EnumHudPosition;
import net.silentchaos512.wit.lib.EnumJustification;

import java.io.File;

public class Config {
    /*
     * HUD display position
     */

    public static EnumJustification hudJustification = EnumJustification.CENTER;
    public static String hudJustificationComment = "The justification of text in the HUD. Can be LEFT, CENTER, or RIGHT";

    public static EnumHudPosition hudPosition = EnumHudPosition.TOP_CENTER;
    public static String hudPositionComment = "The positioning of the HUD. Can be TOP_LEFT, TOP_CENTER, TOP_RIGHT, CENTER_LEFT, CENTER_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, or BOTTOM_RIGHT";

    /*
     * HUD display options
     */

    public static ConfigOptionHudElement hudObjectName = new ConfigOptionHudElement(
            "Object Name", true, false, "§f")
            .setComment("Display the block/entity name in the HUD. Example: Iron Ore, Chicken");
    public static ConfigOptionHudElement hudResourceName = new ConfigOptionHudElement(
            "Registry Name", true, true, "§7")
            .setComment("Display the registry name of the object in the HUD. Example: minecraft:stone.");
    public static ConfigOptionHudElement hudModName = new ConfigOptionHudElement(
            "Mod Name", true, false, "§5")
            .setComment("Display the mod name in the HUD. Example: Minecraft");
//    public static ConfigOptionHudElement hudIdMeta = new ConfigOptionHudElement(
//            "ID and Meta", true, true, "§o")
//            .setComment("Display the ID and metadata in the HUD. Example: [1:0]");
    public static ConfigOptionHudElement hudTileEntity = new ConfigOptionHudElement(
            "Tile Entity", true, false, "§7")
            .setComment("Tells if a tile entity exists for the block being looked at. Example: Furnace (TE)");
    public static ConfigOptionHudElement hudHarvestable = new ConfigOptionHudElement(
            "Harvestability", true, true, "§a", "§c")
            .setComment("Shows whether or not a block is harvestable and with what kind of tool.");
    public static ConfigOptionHudElement hudBlockInventory = new ConfigOptionHudElement(
            "Inventory Contents", true, false, "§f")
            .setComment("Displays the contents of certain inventories, including item count.");
    public static ConfigOptionHudElement hudEntityHealth = new ConfigOptionHudElement(
            "Entity Health", true, false, "§f")
            .setComment("Display the health and max health of entities.");

    public static float hudBackgroundOpacity = 0.8f;
    public static float hudOffsetX = 0.0f;
    public static float hudOffsetY = 0.0f;
    public static boolean hudAdvancedMode = false;
    public static boolean hudHideWhenGuiOpen = true;
    public static int hudInventoryMaxListCount = 8;
    public static boolean disguiseMonsterEggBlocks = true;
    public static boolean enableSunflowerBugfix = true;

    /*
     * Tooltip display options
     */

    public static boolean tooltipDisplayIdMeta = false;
    public static String tooltipDisplayIdMetaComment = "Display the item ID and damage value by the item name.";
    public static boolean tooltipDisplayModName = true;
    public static String tooltipDisplayModNameComment = "Display the name of the mod in tooltips.";
    public static boolean tooltipDisplayModNameShift = false;
    public static String tooltipDisplayModNameShiftComment = "Display the name of the mod only when holding shift.";
    public static boolean tooltipDisplayOreDict = true;
    public static String tooltipDisplayOreDictComment = "Display the ore dictionary entries for the block/item.";
    public static boolean tooltipDisplayOreDictShift = true;
    public static String tooltipDisplayOreDictShiftComment = "Display the ore dictionary entries only when holding shift.";
    public static boolean tooltipDisplayFoodStats = true;
    public static String tooltipDisplayFoodStatsComment = "Display the food value and saturation of foods in the tooltip.";
    public static boolean tooltipDisplayFoodStatsShift = true;
    public static String tooltipDisplayFoodStatsShiftComment = "Display food stats only when holding shift.";
    public static boolean tooltipDisplayToolStats = true;
    public static String tooltipDisplayToolStatsComment = "Display mining speed and durability of tools when possible.";
    public static boolean tooltipDisplayToolStatsShift = true;
    public static String tooltipDisplayToolStatsShiftComment = "Display tool stats only when shift is held.";

    /*
     * Formatting
     */

    public static String formatModName = "&5";
    // Originally: The formatting codes to use in the tooltip. Use & to substitute for the control character.
    public static String formatModNameComment = "Formatting for mod names.";
    public static String formatResourceName = "&7";
    public static String formatResourceNameComment = "Formatting for resource names.";

    private static File configFile;

    public static void init(File file) {
    }

    public static void load() {
        // TODO
    }

    public static void save() {
        // TODO
    }
}
