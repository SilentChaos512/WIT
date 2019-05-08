package net.silentchaos512.wit.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.silentchaos512.wit.config.Config;
import net.silentchaos512.wit.info.ItemStackInfo;

import java.util.List;

public final class TooltipHandler {
    private static TooltipHandler INSTANCE;

    public static void init() {
        if (INSTANCE != null) return;
        INSTANCE = new TooltipHandler();
    }

    private TooltipHandler() {
        MinecraftForge.EVENT_BUS.addListener(TooltipHandler::onTooltip);
    }

    @SuppressWarnings("ChainOfInstanceofChecks")
    private static void onTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().isEmpty() || event.getItemStack().getItem().getRegistryName() == null) {
            // Sometimes this event is fired too early?
            return;
        }

        EntityPlayer player = event.getEntityPlayer();
        if (player == null) return;

        ItemStackInfo itemInfo = new ItemStackInfo(event.getItemStack());
        ItemStack stack = itemInfo.getStack();
        Item item = stack.getItem();

        // Tool stats
        List<ITextComponent> tooltip = event.getToolTip();
        if (item instanceof ItemTool) {
            ItemTool itemTool = (ItemTool) item;
            IBlockState state = getBlockForToolSpeed(itemTool);

            Config.TOOLTIP.elementTool.format(player, () ->
                    new TextComponentTranslation("tooltip.wit.tool.speed",
                            itemTool.getDestroySpeed(stack, state)))
                    .ifPresent(tooltip::add);

            Config.TOOLTIP.elementTool.format(player, () ->
                    new TextComponentTranslation("tooltip.wit.tool.maxDamage",
                            itemTool.getMaxDamage(stack)))
                    .ifPresent(tooltip::add);
        }

        // Food stats
        if (item instanceof ItemFood) {
            ItemFood itemFood = (ItemFood) item;
            Config.TOOLTIP.elementFood.format(player, () ->
                    new TextComponentTranslation("tooltip.wit.food",
                            itemFood.getHealAmount(stack),
                            itemFood.getSaturationModifier(stack)))
                    .ifPresent(tooltip::add);
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
        Config.TOOLTIP.elementModName.format(player, () -> new TextComponentString(itemInfo.getModName()))
                .ifPresent(tooltip::add);
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
