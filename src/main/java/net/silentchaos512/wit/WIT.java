package net.silentchaos512.wit;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.javafmlmod.FMLModLoadingContext;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.silentchaos512.wit.api.WitBlockReplacements;
import net.silentchaos512.wit.client.HudRenderObject;
import net.silentchaos512.wit.client.RayTraceHelper;
import net.silentchaos512.wit.client.key.KeyTracker;
import net.silentchaos512.wit.config.Config;
import net.silentchaos512.wit.info.ItemStackInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

@Mod(WIT.MOD_ID)
public class WIT {
    public static final String MOD_ID = "wit";
    public static final String MOD_NAME = "WIT";
    public static final String VERSION_NUMBER = "1.1.0";

    public static Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public Map<String, ModContainer> mods;
    float lastPartialTicks = 0f;

    public boolean foundStorageDrawers = false;
    public boolean foundMcMultiPart = false;

    // Temporary hack to check that items are initialized
    private boolean initFinished = false;

    public static WIT INSTANCE;
    private static SideProxy PROXY;

    public WIT() {
        INSTANCE = this;
        PROXY = DistExecutor.runForDist(() -> () -> new SideProxy.Client(), () -> () -> new SideProxy.Server());
        FMLModLoadingContext.get().getModEventBus().addListener(this::preInit);
        FMLModLoadingContext.get().getModEventBus().addListener(this::init);
        FMLModLoadingContext.get().getModEventBus().addListener(this::postInit);
//        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, WIT::onTooltip);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, WIT::onRenderOverlay);
    }

    private void preInit(FMLPreInitializationEvent event) {
        PROXY.preInit(event);
//        Config.init(event.getSuggestedConfigurationFile());

        mods = new HashMap<>();
        for (ModInfo info : ModList.get().getMods()) {
            final String modId = info.getModId();
            ModList.get().getModContainerById(modId).ifPresent(c -> mods.put(modId, c));
        }

        populateBlockReplacements();
    }

    private void init(FMLInitializationEvent event) {
        PROXY.init(event);
        Config.save();
        initFinished = true;
    }

    private void postInit(FMLPostInitializationEvent event) {
        PROXY.postInit(event);
        // TODO
//        foundStorageDrawers = Loader.isModLoaded("StorageDrawers");
//        foundMcMultiPart = Loader.isModLoaded("mcmultipart");
    }

//    @SubscribeEvent
//    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
//        if (event.getModID().equals(MOD_ID)) {
//            Config.load();
//            Config.save();
//        }
//    }

    @SuppressWarnings("ChainOfInstanceofChecks")
    private static void onTooltip(ItemTooltipEvent event) {
        if (!INSTANCE.initFinished) return;

        ItemStackInfo itemInfo = new ItemStackInfo(event.getItemStack());
        ItemStack stack = itemInfo.getStack();
        Item item = stack.getItem();

        // Tool stats
        if (shouldDipslayToolStats() && item instanceof ItemTool) {
            ItemTool itemTool = (ItemTool) item;
            IBlockState state = getBlockForToolSpeed(itemTool);
            if (state != null) {
                float speed = itemTool.getDestroySpeed(stack, state);
                event.getToolTip().add(new TextComponentTranslation("tooltip.wit.tool.speed", speed));
            }
            int maxDamage = itemTool.getMaxDamage(stack);
            event.getToolTip().add(new TextComponentTranslation("tooltip.wit.tool.maxDamage", maxDamage));
        }

        // Food stats
        if (shouldDisplayFoodStats() && item instanceof ItemFood) {
            ItemFood itemFood = (ItemFood) item;
            int heal = itemFood.getHealAmount(stack);
            float saturation = itemFood.getSaturationModifier(stack);
            event.getToolTip().add(new TextComponentTranslation("tooltip.wit.food", heal, saturation));
        }

        // Ore dictionary entries
        // TODO: Vanilla tags?
//        if (shouldDisplayOreDict()) {
//            List<String> oreNames = itemInfo.getOreNames();
//            if (!oreNames.isEmpty()) {
//                event.getToolTip().add("Ore Dictionary:");
//                for (String ore : oreNames) {
//                    event.getToolTip().add("- " + ore);
//                }
//            }
//        }

        // Mod name
        if (shouldDisplayModName()) {
            // TODO: Formatting config
            event.getToolTip().add(new TextComponentString(TextFormatting.DARK_PURPLE + itemInfo.getModName()));
//            event.getToolTip().add(Config.formatModName.replaceAll("&", "\u00a7") + itemInfo.getModName());
        }
    }

    private static void onRenderOverlay(RenderGameOverlayEvent event) {
        if (event.isCancelable() || event.getType() != RenderGameOverlayEvent.ElementType.TEXT) {
            return;
        }

        GuiScreen screen = Minecraft.getInstance().currentScreen;
        if (screen != null && !screen.doesGuiPauseGame() && Config.hudHideWhenGuiOpen) {
            return;
        }

        HudRenderObject renderObject = RayTraceHelper.getRenderObject();

        if (renderObject != null) {
            renderObject.render(event);
        } else {
            HudRenderObject.adjustBackgroundHeight(event, HudRenderObject.lastMaxBackgroundHeight, false);
            HudRenderObject.renderBackground(HudRenderObject.lastMaxBackgroundWidth,
                    HudRenderObject.lastBackgroundPosX, HudRenderObject.lastBackgroundPosY);
        }
    }

//    public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
//        // HudRenderObject renderObject = getRenderObject();
//        // TODO ???
//    }

    public void populateBlockReplacements() {
        // TODO: Move into WitBlockReplacments?
        WitBlockReplacements.init();
        WitBlockReplacements rep = WitBlockReplacements.INSTANCE;
        if (Config.disguiseMonsterEggBlocks) {
            rep.add(new ItemStack(Blocks.INFESTED_STONE), new ItemStack(Blocks.STONE));
            rep.add(new ItemStack(Blocks.INFESTED_COBBLESTONE), new ItemStack(Blocks.COBBLESTONE));
            rep.add(new ItemStack(Blocks.INFESTED_CHISELED_STONE_BRICKS), new ItemStack(Blocks.CHISELED_STONE_BRICKS));
            rep.add(new ItemStack(Blocks.INFESTED_CRACKED_STONE_BRICKS), new ItemStack(Blocks.CRACKED_STONE_BRICKS));
            rep.add(new ItemStack(Blocks.INFESTED_MOSSY_STONE_BRICKS), new ItemStack(Blocks.MOSSY_STONE_BRICKS));
            rep.add(new ItemStack(Blocks.INFESTED_STONE_BRICKS), new ItemStack(Blocks.STONE_BRICKS));
        }
    }

    public static boolean shouldDisplayModName() {
        return shouldDisplayObject(Config.tooltipDisplayModName, Config.tooltipDisplayModNameShift);
    }

    public static boolean shouldDisplayOreDict() {
        return shouldDisplayObject(Config.tooltipDisplayOreDict, Config.tooltipDisplayOreDictShift);
    }

    public static boolean shouldDisplayFoodStats() {
        return shouldDisplayObject(Config.tooltipDisplayFoodStats, Config.tooltipDisplayFoodStatsShift);
    }

    public static boolean shouldDipslayToolStats() {
        return shouldDisplayObject(Config.tooltipDisplayToolStats, Config.tooltipDisplayToolStatsShift);
    }

    private static boolean shouldDisplayObject(boolean display, boolean shiftOnly) {
        return display && (!shiftOnly || KeyTracker.INSTANCE.isShiftPressed());
    }

    @SuppressWarnings("ChainOfInstanceofChecks")
    private static IBlockState getBlockForToolSpeed(ItemTool itemTool) {
        if (itemTool instanceof ItemPickaxe) {
            return Blocks.STONE.getDefaultState();
        } else if (itemTool instanceof ItemSpade) {
            return Blocks.DIRT.getDefaultState();
        } else if (itemTool instanceof ItemAxe) {
            return Blocks.OAK_LOG.getDefaultState();
        }
        // Weird generic tool. Naively assume it can mine stone.
        return Blocks.STONE.getDefaultState();
    }
}
