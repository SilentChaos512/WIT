package net.silentchaos512.wit.config;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.fabricmc.loader.FabricLoader;
import net.minecraft.text.TextFormat;
import net.silentchaos512.utils.Anchor;
import net.silentchaos512.utils.config.*;
import net.silentchaos512.wit.lib.TextAlignment;

public final class Config {
    private static final ConfigSpec SPEC = new ConfigSpec();
    private static final CommentedFileConfig CONFIG = CommentedFileConfig.builder(FabricLoader.INSTANCE.getConfigDirectory().toPath().resolve("wit-client.toml").toFile())
            .parsingMode(ParsingMode.REPLACE)
            .writingMode(WritingMode.REPLACE)
            .autoreload()
            .build();
    private static final ConfigSpecWrapper WRAPPER = new ConfigSpecWrapper(CONFIG, SPEC);

    public static final General GENERAL = new General(WRAPPER);
    public static final Hud HUD = new Hud(WRAPPER);
    public static final Tooltip TOOLTIP = new Tooltip(WRAPPER);

    public static class General {
        public final BooleanValue disguiseInfestedBlocks;
        public final BooleanValue advancedMode;

        General(ConfigSpecWrapper wrapper) {
            CONFIG.setComment("general", "General settings");

            disguiseInfestedBlocks = wrapper
                    .builder("general.disguiseInfestedBlocks")
                    .comment("Infested (silverfish) blocks will be shown as their non-infested variants.")
                    .define(true);
            advancedMode = wrapper
                    .builder("general.showAdvancedInfo")
                    .comment("Display additional information which may not be useful to most players.")
                    .define(false);
        }
    }

    public static class Hud {
        public final DoubleValue backgroundOpacity;
        public final BooleanValue hideWhenGuiOpen;
        public final IntValue inventoryMaxLines;
        public final IntValue offsetX;
        public final IntValue offsetY;
        public final DoubleValue blockStickyTime;
        public final DoubleValue entityStickyTime;
        public final EnumValue<Anchor> position;
        public final EnumValue<TextAlignment> textAlignment;

        public final ConfigValueTextElement elementEntityHealth;
        public final ConfigValueTextElement elementEntityArmor;
        public final ConfigValueTextElement elementHarvest;
        public final ConfigValueTextElement elementInventoryContents;
        public final ConfigValueTextElement elementModName;
        public final ConfigValueTextElement elementName;
        public final ConfigValueTextElement elementRegistryName;
        public final ConfigValueTextElement elementTileEntityMarker;
        public final ConfigValueTextElement elementCropGrowth;

        Hud(ConfigSpecWrapper wrapper) {
            wrapper.comment("hud", "HUD overlay settings");

            backgroundOpacity = wrapper
                    .builder("hud.backgroundOpacity")
                    .comment("Opacity of overlay background. 0 = fully transparent, 1 = fully opaque.")
                    .defineInRange(0.8, 0, 1);

            hideWhenGuiOpen = wrapper
                    .builder("hud.hideWhenGuiOpen")
                    .comment("Hide the HUD overlay when a GUI is open. Does not apply to any GUI that pauses the game.")
                    .define(true);

            inventoryMaxLines = wrapper
                    .builder("hud.inventoryMaxLines")
                    .comment("The maximum number of items to display in inventory contents lists.")
                    .defineInRange(4, 0, 100);

            offsetX = wrapper
                    .builder("hud.position.offsetX")
                    .comment("Offset for HUD position. Change if you need to fine-tune the position.")
                    .defineInRange(0, Integer.MIN_VALUE, Integer.MAX_VALUE);

            offsetY = wrapper
                    .builder("hud.position.offsetY")
                    .comment("Offset for HUD position. Change if you need to fine-tune the position.")
                    .defineInRange(0, Integer.MIN_VALUE, Integer.MAX_VALUE);

            wrapper.comment("stickyTime",
                    "Sticky time is the length of time (in seconds) the HUD overlay will remain",
                    "open after the target is out of range. Handy when entities can't be still!");

            blockStickyTime = wrapper
                    .builder("hud.stickyTime.block")
                    .defineInRange(1, 0, Double.MAX_VALUE);
            entityStickyTime = wrapper
                    .builder("hud.stickyTime.entity")
                    .defineInRange(4, 0, Double.MAX_VALUE);

            position = wrapper
                    .builder("hud.position.anchor")
                    .comment("Position of the HUD overlay",
                            EnumValue.allValuesComment(Anchor.class))
                    .defineEnum(Anchor.TOP_CENTER);
            textAlignment = wrapper
                    .builder("hud.textAlignment")
                    .comment("Alignment of text in the HUD overlay.",
                            EnumValue.allValuesComment(TextAlignment.class))
                    .defineEnum(TextAlignment.CENTER);

            wrapper.comment("hud.elements",
                    "Settings for what is displayed in the HUD overlay.",
                    "Valid values for \"show\": ALWAYS, NEVER, SNEAK_ONLY, CTRL_KEY, ALT_KEY");

            elementEntityArmor = ConfigValueTextElement.define(wrapper,
                    "hud.elements.entityArmor",
                    "Show the armor value of entities.",
                    ConfigValueTextElement.ShowCondition.ALWAYS,
                    TextFormat.WHITE);

            elementEntityHealth = ConfigValueTextElement.define(wrapper,
                    "hud.elements.entityHealth",
                    "Show the health of entities.",
                    ConfigValueTextElement.ShowCondition.ALWAYS,
                    TextFormat.WHITE);

            elementHarvest = ConfigValueTextElement.define(wrapper,
                    "hud.elements.harvestInfo",
                    "Show the harvest level and tool of blocks.",
                    ConfigValueTextElement.ShowCondition.SNEAK_ONLY,
                    TextFormat.GREEN,
                    TextFormat.RED);

            elementInventoryContents = ConfigValueTextElement.define(wrapper,
                    "hud.elements.inventoryContents",
                    "Show some of the items in inventory blocks.",
                    ConfigValueTextElement.ShowCondition.ALWAYS,
                    TextFormat.WHITE);

            elementModName = ConfigValueTextElement.define(wrapper,
                    "hud.elements.modName",
                    "Show the name of the mod.",
                    ConfigValueTextElement.ShowCondition.ALWAYS,
                    TextFormat.DARK_PURPLE);

            elementName = ConfigValueTextElement.define(wrapper,
                    "hud.elements.name",
                    "Show the name of the block or entity.",
                    ConfigValueTextElement.ShowCondition.ALWAYS,
                    TextFormat.BOLD);

            elementRegistryName = ConfigValueTextElement.define(wrapper,
                    "hud.elements.registryName",
                    "Show the registry (internal) name of the block or entity.",
                    ConfigValueTextElement.ShowCondition.SNEAK_ONLY,
                    TextFormat.DARK_GRAY);

            elementTileEntityMarker = ConfigValueTextElement.define(wrapper,
                    "hud.elements.blockEntityMarker",
                    "Show \"[BE]\" next to the name of block (tile) entities.",
                    ConfigValueTextElement.ShowCondition.ALWAYS,
                    TextFormat.DARK_GRAY);

            elementCropGrowth = ConfigValueTextElement.define(wrapper,
                    "hud.elements.cropGrowth",
                    "Show age of crops",
                    ConfigValueTextElement.ShowCondition.ALWAYS,
                    TextFormat.GREEN,
                    TextFormat.YELLOW);
        }
    }

    public static class Tooltip {
        public final ConfigValueTextElement elementFood;
        public final ConfigValueTextElement elementModName;
        //        public final ConfigValueTextElement elementTags;
        public final ConfigValueTextElement elementTool;

        Tooltip(ConfigSpecWrapper wrapper) {
            wrapper.comment("tooltips",
                    "Settings for additional tooltip information",
                    "Valid values for \"show\": ALWAYS, NEVER, SHIFT_KEY, CTRL_KEY, ALT_KEY");

            elementFood = ConfigValueTextElement.define(wrapper,
                    "tooltips.foodStats",
                    "Show food properties",
                    ConfigValueTextElement.ShowCondition.ALWAYS,
                    TextFormat.GRAY);

            elementModName = ConfigValueTextElement.define(wrapper,
                    "tooltips.modName",
                    "Show the mod name",
                    ConfigValueTextElement.ShowCondition.ALWAYS,
                    TextFormat.DARK_PURPLE);

//            elementTags = ConfigValueTextElement.define(builder,
//                    "tags",
//                    "Show item tags",
//                    ConfigValueTextElement.ShowCondition.SHIFT_KEY,
//                    TextFormat.GRAY);

            elementTool = ConfigValueTextElement.define(wrapper,
                    "tooltips.toolStats",
                    "Show basic properties of harvest tools",
                    ConfigValueTextElement.ShowCondition.SHIFT_KEY,
                    TextFormat.GRAY);
        }
    }

    private Config() {}

    public static void init() {
        WRAPPER.validate();
    }
}
