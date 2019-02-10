package net.silentchaos512.wit.info;

import net.fabricmc.fabric.api.tag.FabricItemTags;
import net.fabricmc.fabric.impl.tools.ToolManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.Tag;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.silentchaos512.wit.api.InfoCallbacks;
import net.silentchaos512.wit.api.WitHudInfoEvent;
import net.silentchaos512.wit.config.Config;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class BlockStackInfo extends ItemStackInfo {
    private final BlockState state;
    private final BlockPos pos;
    private final Block block;
    private final BlockEntity blockEntity;
    @Nullable private final Tag<Item> harvestTool;
    private final int harvestLevel;
    private boolean isPickBlock = true;

    public BlockStackInfo(BlockView world, BlockState state, BlockPos pos) {
        this(new ItemStack(state.getBlock()), world, state, pos);
        isPickBlock = false;
    }

    public BlockStackInfo(ItemStack stack, BlockView world, BlockState state, BlockPos pos) {
        super(stack);
        this.state = state;
        this.block = this.state.getBlock();
        this.pos = pos;
        this.blockEntity = tryGetTileEntity(world, this.pos);
        this.harvestTool = getHarvestTool(state);
        this.harvestLevel = getHarvestLevel(state);
    }

    @Nullable
    private static Tag<Item> getHarvestTool(BlockState state) {
        if (ToolManager.handleIsEffectiveOn(new ItemStack(Items.STICK), state).get())
            return null;
        if (ToolManager.handleIsEffectiveOn(new ItemStack(Items.DIAMOND_PICKAXE), state).get())
            return FabricItemTags.PICKAXES;
        if (ToolManager.handleIsEffectiveOn(new ItemStack(Items.DIAMOND_SHOVEL), state).get())
            return FabricItemTags.SHOVELS;
        if (ToolManager.handleIsEffectiveOn(new ItemStack(Items.DIAMOND_AXE), state).get())
            return FabricItemTags.AXES;
        if (ToolManager.handleIsEffectiveOn(new ItemStack(Items.DIAMOND_SWORD), state).get())
            return FabricItemTags.SWORDS;
        if (ToolManager.handleIsEffectiveOn(new ItemStack(Items.DIAMOND_HOE), state).get())
            return FabricItemTags.HOES;
        return null;
    }

    private static int getHarvestLevel(BlockState state) {
        // TODO
        return -1;
    }

    @Nullable
    private static BlockEntity tryGetTileEntity(BlockView world, BlockPos pos) {
        return world.getBlockEntity(pos);
    }

    @Override
    public void addLines(PlayerEntity player, List<TextComponent> lines) {
        // Name, tile entity
        Config.HUD.elementName.format(player, this::displayItemName).ifPresent(text ->
                lines.add(displayBlockName(player, text)));

        // Mob spawner?
        getLinesForMobSpawner(lines);
        // Tank?
        getLinesForTank(lines);
        // FE storage?
        getLinesForEnergyStorage(lines);
        // Inventory?
        getLinesForBlockInventory(player, lines);

        // Harvestability
        getLinesForBlockHarvestability(player, lines);

        // Registry name
        Config.HUD.elementRegistryName.format(player, this::displayRegistryName).ifPresent(lines::add);

        // Wit HUD info event
        InfoCallbacks.postBlockInfo(new WitHudInfoEvent<>(player, player.world, this, lines, Config.GENERAL.advancedMode::get));

        // Mod name
        Config.HUD.elementModName.format(player, this::displayModName).ifPresent(lines::add);
    }

    @Override
    TextComponent displayItemName() {
        return isPickBlock ? block.getTextComponent() : super.displayItemName();
    }

    @Override
    TextComponent displayRegistryName() {
        if (isPickBlock) {
            Identifier id = Registry.BLOCK.getId(block);
            return new StringTextComponent(id.toString());
        }
        return super.displayRegistryName();
    }

    private TextComponent displayBlockName(PlayerEntity player, TextComponent text) {
        if (blockEntity != null) {
            Supplier<TextComponent> marker = () -> new StringTextComponent(" [BE]");
            Config.HUD.elementTileEntityMarker.format(player, marker).ifPresent(text::append);
        }
        return text;
    }

    private void getLinesForBlockInventory(PlayerEntity player, List<TextComponent> lines) {
        if (!Config.HUD.elementInventoryContents.isShownFor(player) || !(this.blockEntity instanceof Inventory)) {
            return;
        }

        // Storage Drawers?
//        if (Wit.INSTANCE.foundStorageDrawers && blockEntity instanceof IDrawerGroup) {
//            IDrawerGroup drawers = (IDrawerGroup) blockEntity;
//            for (int i = 0; i < drawers.getDrawerCount(); ++i) {
//                IDrawer d = drawers.getDrawer(i);
//                if (d != null && d.getStoredItemPrototype() != null) {
//                    int count = d.getStoredItemCount();
//                    String name = d.getStoredItemPrototype().getDisplayName();
//                    lines.add(Config.hudBlockInventory.formatString(String.format(str, name, count)));
//                }
//            }
//        }
        // Generic inventory handler.
        // else {
        List<ItemStack> invStacks = getInventoryStacks((Inventory) blockEntity);
        // Display first n items according to config setting.
        for (int i = 0; i < invStacks.size() && i < Config.HUD.inventoryMaxLines.get(); ++i) {
            ItemStack stack = invStacks.get(i);
            try {
                int count = stack.getAmount();
                Config.HUD.elementInventoryContents.format(player, () ->
                        new TranslatableTextComponent("hud.wit.inventory.itemCount", stack.getDisplayName(), count))
                        .ifPresent(lines::add);
            } catch (Exception ex) {
                // Looks like a mod has done something stupid with their items...
                lines.add(new StringTextComponent(TextFormat.RED + "ERROR: " + stack.getItem().getTranslationKey()));
            }
        }
        // How many did we not display?
        int omittedCount = invStacks.size() - Config.HUD.inventoryMaxLines.get();
        if (omittedCount > 0) {
            lines.add(new TranslatableTextComponent("wit.OmittedInventoryItems", omittedCount));
        }
        // }
    }

    private static DefaultedList<ItemStack> getInventoryStacks(Inventory inv) {
        DefaultedList<ItemStack> list = DefaultedList.create();
        for (int i = 0; i < inv.getInvSize(); ++i) {
            ItemStack stack = inv.getInvStack(i);
            if (!stack.isEmpty()) {
                list.add(stack);
            }
        }
        return list;
    }

    private void getLinesForBlockHarvestability(PlayerEntity player, List<TextComponent> lines) {
        if (!Config.HUD.elementHarvest.isShownFor(player)) return;

//        boolean canHarvest = ForgeHooks.canHarvestBlock(state, player, player.world, pos)
//                && state.getBlockHardness(player.world, pos) >= 0;
        boolean canHarvest = player.getMainHandStack().getItem().isEffectiveOn(state);

        if (harvestTool != null && harvestLevel > -1) {
            // Known tool
            TextComponent tool = new TranslatableTextComponent("hud.wit.tool." + harvestTool);
            if (harvestLevel > 0) {
                // Specific level
                Config.HUD.elementHarvest.formatEither(player, () ->
                        new TranslatableTextComponent("hud.wit.harvestWithLevel", tool, harvestLevel), canHarvest)
                        .ifPresent(lines::add);
            } else {
                // Any/unknown level
                Config.HUD.elementHarvest.formatEither(player, () ->
                        new TranslatableTextComponent("hud.wit.harvestWith", tool), canHarvest)
                        .ifPresent(lines::add);
            }
        } else {
            // Unknown tool, but we know whether or not it can be harvested.
            Supplier<TextComponent> text = () ->
                    new TranslatableTextComponent("hud.wit.harvestable." + canHarvest);
            Config.HUD.elementHarvest.formatEither(player, text, canHarvest).ifPresent(t -> {
                String mark = " " + (canHarvest ? "\u2713" : "\u2717"); // check mark or x
                lines.add(t.append(mark));
            });
        }
    }

    private void getLinesForTank(List<TextComponent> lines) {
/*        if (!(blockEntity instanceof IFluidTank)) return;

        IFluidTank tank = (IFluidTank) blockEntity;
        int amount = tank.getFluidAmount();
        int capacity = tank.getCapacity();
        lines.add(new TranslatableTextComponent("hud.wit.tile.tankStorage", amount, capacity));*/
    }

    private void getLinesForMobSpawner(List<TextComponent> lines) {
        if (!(blockEntity instanceof MobSpawnerBlockEntity)) return;

        MobSpawnerBlockEntity tile = (MobSpawnerBlockEntity) blockEntity;
        Entity entity = tile.getLogic().getRenderedEntity();
        if (entity != null) {
            lines.add(entity.getDisplayName());
        }
    }

    private void getLinesForEnergyStorage(List<TextComponent> lines) {
/*        if (!(this.blockEntity instanceof IEnergyStorage)) return;

        IEnergyStorage energyStorage = (IEnergyStorage) this.blockEntity;
        int current = energyStorage.getEnergyStored();
        int max = energyStorage.getMaxEnergyStored();
        lines.add(new TranslatableTextComponent("hud.wit.tile.feStorage", current, max));*/
    }

    public BlockState getState() {
        return state;
    }

    public BlockPos getPos() {
        return pos;
    }

    public Block getBlock() {
        return block;
    }

    public BlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Nullable
    public Tag<Item> getHarvestTool() {
        return harvestTool;
    }

    public int getHarvestLevel() {
        return harvestLevel;
    }
}
