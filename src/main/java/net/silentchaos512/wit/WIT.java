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
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.wit.client.HudRenderObject;
import net.silentchaos512.wit.client.RayTraceHelper;
import net.silentchaos512.wit.client.key.KeyTracker;
import net.silentchaos512.wit.config.Config;
import net.silentchaos512.wit.info.ItemStackInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(WIT.MOD_ID)
public class WIT {
    public static final String MOD_ID = "wit";
    public static final String MOD_NAME = "WIT";
    public static final String VERSION_NUMBER = "1.1.0";

    public static Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public boolean foundStorageDrawers = false;
    public boolean foundMcMultiPart = false;

    public static WIT INSTANCE;
    private static SideProxy PROXY;

    public WIT() {
        INSTANCE = this;
        PROXY = DistExecutor.runForDist(() -> () -> new SideProxy.Client(), () -> () -> new SideProxy.Server());
        Config.load();

        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, WIT::onTooltip);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, WIT::onRenderOverlay);
    }

    @SuppressWarnings("ChainOfInstanceofChecks")
    private static void onTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().isEmpty() || event.getItemStack().getItem().getRegistryName() == null) {
            // Sometimes this event is fired too early?
            return;
        }

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
        if (screen != null && !screen.doesGuiPauseGame() && Config.HUD.hideWhenGuiOpen.get()) {
            return;
        }

        HudRenderObject renderObject = RayTraceHelper.getRenderObject(event.getPartialTicks());

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
