package net.silentchaos512.wit.info;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.IFluidTank;
import net.silentchaos512.wit.api.WitBlockInfoEvent;
import net.silentchaos512.wit.config.Config;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class BlockStackInfo extends ItemStackInfo {
    private final IBlockState state;
    private final BlockPos pos;
    private final Block block;
    private final TileEntity tileEntity;
    private final ToolType harvestTool;
    private final int harvestLevel;

    public BlockStackInfo(World world, IBlockState state, BlockPos pos) {
        super(new ItemStack(state.getBlock()));

        this.state = state;
        this.block = this.state.getBlock();
        this.pos = pos;
        this.tileEntity = tryGetTileEntity(world, this.pos);
        this.harvestTool = this.state.getHarvestTool();
        this.harvestLevel = this.state.getHarvestLevel();
    }

    @Nullable
    private static TileEntity tryGetTileEntity(World world, BlockPos pos) {
        return world.getTileEntity(pos);
    }

    @Override
    public void addLines(EntityPlayer player, List<ITextComponent> lines) {
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

        // WIT HUD info event
        processInfoEvent(lines, new WitBlockInfoEvent(player, player.world, Config.GENERAL.advancedMode.get(), pos, state));

        // Mod name
        Config.HUD.elementModName.format(player, this::displayModName).ifPresent(lines::add);
    }

    private ITextComponent displayBlockName(EntityPlayer player, ITextComponent text) {
        if (tileEntity != null) {
            Supplier<ITextComponent> marker = () -> new TextComponentString(" [TE]");
            Config.HUD.elementTileEntityMarker.format(player, marker).ifPresent(text::appendSibling);
        }
        return text;
    }

    private void getLinesForBlockInventory(EntityPlayer player, List<ITextComponent> lines) {
        if (!Config.HUD.elementInventoryContents.isShownFor(player) || !(this.tileEntity instanceof IInventory)) {
            return;
        }

        // Storage Drawers?
//        if (WIT.INSTANCE.foundStorageDrawers && tileEntity instanceof IDrawerGroup) {
//            IDrawerGroup drawers = (IDrawerGroup) tileEntity;
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
        List<ItemStack> invStacks = getInventoryStacks((IInventory) tileEntity);
        // Display first n items according to config setting.
        for (int i = 0; i < invStacks.size() && i < Config.HUD.inventoryMaxLines.get(); ++i) {
            ItemStack stack = invStacks.get(i);
            try {
                int count = stack.getCount();
                Config.HUD.elementInventoryContents.format(player, () ->
                        new TextComponentTranslation("hud.wit.inventory.itemCount", stack.getDisplayName(), count))
                        .ifPresent(lines::add);
            } catch (Exception ex) {
                // Looks like a mod has done something stupid with their items...
                lines.add(new TextComponentString(TextFormatting.RED + "ERROR: " + stack.getItem().getRegistryName()));
            }
        }
        // How many did we not display?
        int omittedCount = invStacks.size() - Config.HUD.inventoryMaxLines.get();
        if (omittedCount > 0) {
            lines.add(new TextComponentTranslation("wit.OmittedInventoryItems", omittedCount));
        }
        // }
    }

    private static NonNullList<ItemStack> getInventoryStacks(IInventory inv) {
        NonNullList<ItemStack> list = NonNullList.create();
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                list.add(stack);
            }
        }
        return list;
    }

    private void getLinesForBlockHarvestability(EntityPlayer player, List<ITextComponent> lines) {
        if (!Config.HUD.elementHarvest.isShownFor(player)) return;

        boolean canHarvest = ForgeHooks.canHarvestBlock(state, player, player.world, pos)
                && state.getBlockHardness(player.world, pos) >= 0;

        if (harvestTool != null && harvestLevel > -1) {
            // Known tool
            ITextComponent tool = new TextComponentTranslation("hud.wit.tool." + harvestTool);
            if (harvestLevel > 0) {
                // Specific level
                Config.HUD.elementHarvest.formatEither(player, () ->
                        new TextComponentTranslation("hud.wit.harvestWithLevel", tool, harvestLevel), canHarvest)
                        .ifPresent(lines::add);
            } else {
                // Any/unknown level
                Config.HUD.elementHarvest.formatEither(player, () ->
                        new TextComponentTranslation("hud.wit.harvestWith", tool), canHarvest)
                        .ifPresent(lines::add);
            }
        } else {
            // Unknown tool, but we know whether or not it can be harvested.
            Supplier<ITextComponent> text = () ->
                    new TextComponentTranslation("hud.wit.harvestable." + canHarvest);
            Config.HUD.elementHarvest.formatEither(player, text, canHarvest).ifPresent(t -> {
                String mark = " " + (canHarvest ? "\u2713" : "\u2717"); // check mark or x
                lines.add(t.appendText(mark));
            });
        }
    }

    private void getLinesForTank(List<ITextComponent> lines) {
        if (!(tileEntity instanceof IFluidTank)) return;

        IFluidTank tank = (IFluidTank) tileEntity;
        int amount = tank.getFluidAmount();
        int capacity = tank.getCapacity();
        lines.add(new TextComponentTranslation("hud.wit.tile.tankStorage", amount, capacity));
    }

    private void getLinesForMobSpawner(List<ITextComponent> lines) {
        if (!(tileEntity instanceof TileEntityMobSpawner)) return;

        TileEntityMobSpawner tile = (TileEntityMobSpawner) tileEntity;
        Entity entity = tile.getSpawnerBaseLogic().getCachedEntity();
        if (entity != null) {
            lines.add(entity.getDisplayName());
        }
    }

    private void getLinesForEnergyStorage(List<ITextComponent> lines) {
        if (!(this.tileEntity instanceof IEnergyStorage)) return;

        IEnergyStorage energyStorage = (IEnergyStorage) this.tileEntity;
        int current = energyStorage.getEnergyStored();
        int max = energyStorage.getMaxEnergyStored();
        lines.add(new TextComponentTranslation("hud.wit.tile.feStorage", current, max));
    }

    public IBlockState getState() {
        return state;
    }

    public BlockPos getPos() {
        return pos;
    }

    public Block getBlock() {
        return block;
    }

    public TileEntity getTileEntity() {
        return tileEntity;
    }

    public ToolType getHarvestTool() {
        return harvestTool;
    }

    public int getHarvestLevel() {
        return harvestLevel;
    }
}
