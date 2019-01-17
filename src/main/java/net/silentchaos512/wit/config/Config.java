package net.silentchaos512.wit.config;

import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.loading.FMLPaths;
import net.silentchaos512.wit.WIT;
import net.silentchaos512.wit.lib.HudAnchor;
import net.silentchaos512.wit.lib.TextAlignment;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.function.Supplier;

public final class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final General GENERAL = new General(BUILDER);
    public static final Hud HUD = new Hud(BUILDER);
    public static final Tooltip TOOLTIP = new Tooltip(BUILDER);

    public static class General {
        public final BooleanValue disguiseInfestedBlocks;
        public final BooleanValue advancedMode;

        General(ForgeConfigSpec.Builder builder) {
            builder.comment("General settings")
                    .push("general");

            disguiseInfestedBlocks = builder
                    .comment("Infested (silverfish) blocks will be shown as their non-infested variants.")
                    .define("disguiseInfestedBlocks", true);

            advancedMode = builder
                    .comment("Display additional information which may not be useful to most players.")
                    .define("advancedMode", false);

            builder.pop(); //general
        }
    }

    public static class Hud {
        public final DoubleValue backgroundOpacity;
        public final BooleanValue hideWhenGuiOpen;
        public final IntValue inventoryMaxLines;
        public final IntValue offsetX;
        public final IntValue offsetY;
        // TODO: Change back to ConfigValue<...> when defineEnum is fixed
        public final Supplier<HudAnchor> position;
        public final Supplier<TextAlignment> textAlignment;

        public final ConfigValueTextElement elementEntityHealth;
        public final ConfigValueTextElement elementEntityArmor;
        public final ConfigValueTextElement elementHarvest;
        public final ConfigValueTextElement elementInventoryContents;
        public final ConfigValueTextElement elementModName;
        public final ConfigValueTextElement elementName;
        public final ConfigValueTextElement elementRegistryName;
        public final ConfigValueTextElement elementTileEntityMarker;

        Hud(ForgeConfigSpec.Builder builder) {
            builder.comment("HUD overlay settings")
                    .push("hud");

            backgroundOpacity = builder
                    .comment("Opacity of the background image. 0 = fully transparent, 1 = fully opaque.")
                    .defineInRange("backgroundOpacity", 0.8, 0, 1);

            hideWhenGuiOpen = builder
                    .comment("Hide the HUD overlay when a GUI is open. Does not apply to any GUI that pauses the game.")
                    .define("hideWhenGuiOpen", true);

            inventoryMaxLines = builder
                    .comment("The maximum number of items to display in inventory contents lists.")
                    .defineInRange("inventoryMaxLines", 4, 0, 100);

            offsetX = builder
                    .comment("Offset for HUD position. Change if you need to fine-tune the position.")
                    .defineInRange("position.offsetX", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

            offsetY = builder
                    .comment("Offset for HUD position. Change if you need to fine-tune the position.")
                    .defineInRange("position.offsetY", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

//            position = builder
//                    .comment("The position of the HUD overlay.",
//                            validValuesComment(HudAnchor.class))
//                    .defineEnum("position.anchor", HudAnchor.TOP_CENTER);
            builder.comment("The position of the HUD overlay.", validValuesComment(HudAnchor.class));
            position = defineEnumWorkaround(builder, "position.anchor", HudAnchor.TOP_CENTER);

//            textAlignment = builder
//                    .comment("Alignment (justification) of text in the HUD overlay.",
//                            validValuesComment(TextAlignment.class))
//                    .defineEnum("textAlignment", TextAlignment.CENTER);
            builder.comment("Alignment of text in the HUD overlay.", validValuesComment(TextAlignment.class));
            textAlignment = defineEnumWorkaround(builder, "textAlignment", TextAlignment.CENTER);

            builder.comment("Settings for what is displayed in the HUD overlay.")
                    .push("elements");

            elementEntityArmor = ConfigValueTextElement.define(builder,
                    "entityArmor",
                    "Show the armor value of entities.",
                    ConfigValueTextElement.ShowCondition.ALWAYS,
                    TextFormatting.WHITE);

            elementEntityHealth = ConfigValueTextElement.define(builder,
                    "entityHealth",
                    "Show the health of entities.",
                    ConfigValueTextElement.ShowCondition.ALWAYS,
                    TextFormatting.WHITE);

            elementHarvest = ConfigValueTextElement.define(builder,
                    "harvestInfo",
                    "Show the harvest level and tool of blocks.",
                    ConfigValueTextElement.ShowCondition.SNEAK_ONLY,
                    TextFormatting.GREEN,
                    TextFormatting.RED);

            elementInventoryContents = ConfigValueTextElement.define(builder,
                    "inventoryContents",
                    "Show some of the items in inventory blocks.",
                    ConfigValueTextElement.ShowCondition.ALWAYS,
                    TextFormatting.WHITE);

            elementModName = ConfigValueTextElement.define(builder,
                    "modName",
                    "Show the name of the mod.",
                    ConfigValueTextElement.ShowCondition.ALWAYS,
                    TextFormatting.DARK_PURPLE);

            elementName = ConfigValueTextElement.define(builder,
                    "name",
                    "Show the name of the block or entity.",
                    ConfigValueTextElement.ShowCondition.ALWAYS,
                    TextFormatting.BOLD);

            elementRegistryName = ConfigValueTextElement.define(builder,
                    "registryName",
                    "Show the registry (internal) name of the block or entity.",
                    ConfigValueTextElement.ShowCondition.SNEAK_ONLY,
                    TextFormatting.DARK_GRAY);

            elementTileEntityMarker = ConfigValueTextElement.define(builder,
                    "tileEntityMarker",
                    "Show \"[TE]\" next to the name of tile entities.",
                    ConfigValueTextElement.ShowCondition.ALWAYS,
                    TextFormatting.DARK_GRAY);

            builder.pop(); //elements

            builder.pop(); //hud
        }
    }

    public static class Tooltip {
        Tooltip(ForgeConfigSpec.Builder builder) {
            ;
        }
    }

    private static final ForgeConfigSpec spec = BUILDER.build();

    private Config() { }

    private static void loadFrom(final Path configRoot) {
        Path configFile = configRoot.resolve(WIT.MOD_ID + ".toml");
        spec.setConfigFile(configFile);
        WIT.LOGGER.debug("Loaded config from {}", configFile);
    }

    public static void load() {
        loadFrom(FMLPaths.CONFIGDIR.get());
    }

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

    // Enum helper methods

    private static <E extends Enum<E>> String validValuesComment(Class<E> enumClass) {
        StringBuilder builder = new StringBuilder().append("Valid values: [");
        E[] enumConstants = enumClass.getEnumConstants();
        for (int i = 0; i < enumConstants.length; ++i) {
            builder.append(enumConstants[i].name());
            if (i < enumConstants.length - 1) {
                builder.append(", ");
            }
        }
        return builder.append("]").toString();
    }

    // FIXME: Forge build 83 defineEnum does not work. This is a workaround.
    @Deprecated // remove when issue fixed
    static <E extends Enum<E>> Supplier<E> defineEnumWorkaround(ForgeConfigSpec.Builder builder, String path, E defaultValue) {
        Class<E> enumClass = defaultValue.getDeclaringClass();
        ConfigValue<String> configValue = builder
                .define(path, defaultValue::name, o -> validateEnum(o, enumClass));
        return () -> getEnumByName(configValue.get(), defaultValue);
    }

    @Deprecated // remove when issue fixed
    private static <E extends Enum<E>> E getEnumByName(String name, E defaultValue) {
        for (E e : defaultValue.getDeclaringClass().getEnumConstants()) {
            if (e.name().equalsIgnoreCase(name)) {
                return e;
            }
        }
        return defaultValue;
    }

    @Deprecated // remove when issue fixed
    private static <E extends Enum<E>> boolean validateEnum(@Nullable Object obj, Class<E> enumClass) {
        if (obj == null) return false;
        for (E e : enumClass.getEnumConstants()) {
            if (e.name().equalsIgnoreCase(obj.toString())) {
                return true;
            }
        }
        return false;
    }
}
