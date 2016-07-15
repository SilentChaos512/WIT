package net.silentchaos512.wit.info;

import java.util.List;

import com.google.common.collect.Lists;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.silentchaos512.wit.WIT;
import net.silentchaos512.wit.api.IWitHudInfo;
import net.silentchaos512.wit.api.WitBlockInfoEvent;
import net.silentchaos512.wit.config.Config;
import net.silentchaos512.wit.lib.LocalizationHelper;

public class BlockStackInfo extends ItemStackInfo {

  // Block
  @Getter(value = AccessLevel.PUBLIC)
  IBlockState state;
  @Getter(value = AccessLevel.PUBLIC)
  BlockPos pos;
  @Getter(value = AccessLevel.PUBLIC)
  Block block;
  @Getter(value = AccessLevel.PUBLIC)
  int blockId;
  @Getter(value = AccessLevel.PUBLIC)
  int meta;
  @Getter(value = AccessLevel.PUBLIC)
  TileEntity tileEntity;
  @Getter(value = AccessLevel.PUBLIC)
  String harvestTool;
  @Getter(value = AccessLevel.PUBLIC)
  int harvestLevel;

  public BlockStackInfo(IBlockState state, BlockPos pos) {

    super(new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)));

    Block newBlock = Block.getBlockFromItem(item);
    meta = stack.getItemDamage();
    IBlockState newState = newBlock.getStateFromMeta(meta);

    this.state = newState;
    this.pos = pos;
    block = newBlock;
    blockId = block.getIdFromBlock(block);
    // meta = block.getMetaFromState(state);
    tileEntity = Minecraft.getMinecraft().thePlayer.worldObj.getTileEntity(pos);
    boolean metaIsGood = meta >= 0 && meta < 16;
    harvestTool = metaIsGood ? block.getHarvestTool(newState) : "null";
    harvestLevel = metaIsGood ? block.getHarvestLevel(newState) : -1;
  }

  @Override
  public void addLines(EntityPlayer player, List<String> lines) {

    boolean isIInventory = tileEntity instanceof IInventory;
    boolean isIWitHudInfo = block instanceof IWitHudInfo;

    // Name, ID, meta, tile entity
    String line = Config.hudObjectName.shouldDisplay(player)
        ? item.getRarity(stack).rarityColor + localizedName : "";
    line += Config.hudIdMeta.shouldDisplay(player)
        ? Config.hudIdMeta.formatString(" [" + blockId + ":" + meta + "]") : "";
    if (tileEntity != null && Config.hudTileEntity.shouldDisplay(player)) {
      line += Config.hudTileEntity.formatString(" (TE)");
    }
    lines.add(line);

    // Inventory?
    getLinesForBlockInventory(isIInventory, isIWitHudInfo, player, lines);
    // Mob spawner?
    getLinesForMobSpawner(lines);
    // RF storage?
    getLinesForRfEnergyHandler(lines);

    // Harvestability
    getLinesForBlockHarvestability(player, lines);

    // Full (resource) name
    if (Config.hudResourceName.shouldDisplay(player)) {
      lines.add(
          Config.hudResourceName.formatString(modId + ":" + resourceLocation.getResourcePath()));
    }

    // Block specific info?
    if (isIWitHudInfo) {
      List<String> extraList = ((IWitHudInfo) block).getWitLines(state, pos, player,
          Config.hudAdvancedMode);
      if (extraList != null) {
        lines.addAll(extraList);
      }
    }

    // WIT HUD info event
    WitBlockInfoEvent event = new WitBlockInfoEvent(player, Config.hudAdvancedMode, pos, state);
    if (!MinecraftForge.EVENT_BUS.post(event)) {
      lines.addAll(event.lines);
    }

    // Mod name
    if (Config.hudModName.shouldDisplay(player)) {
      lines.add(Config.hudModName.formatString(modName));
    }
  }

  public void getLinesForBlockInventory(boolean isIInventory, boolean isIWitHudInfo,
      EntityPlayer player, List<String> lines) {

    if (!Config.hudBlockInventory.shouldDisplay(player)) {
      return;
    }

    String str = "%s (%d)";

    // Storage Drawers?
    if (WIT.instance.foundStorageDrawers && tileEntity instanceof IDrawerGroup) {
      IDrawerGroup drawers = (IDrawerGroup) tileEntity;
      for (int i = 0; i < drawers.getDrawerCount(); ++i) {
        IDrawer d = drawers.getDrawer(i);
        if (d != null && d.getStoredItemPrototype() != null) {
          int count = d.getStoredItemCount();
          String name = d.getStoredItemPrototype().getDisplayName();
          lines.add(Config.hudBlockInventory.formatString(String.format(str, name, count)));
        }
      }
    }
    // Generic inventory handler.
    else if (isIInventory && !isIWitHudInfo) {
      List<ItemStack> invStacks = getInventoryStacks((IInventory) tileEntity);
      // Display first n items according to config setting.
      for (int i = 0; i < invStacks.size() && i < Config.hudInventoryMaxListCount; ++i) {
        ItemStack stack = invStacks.get(i);
        if (stack != null) {
          try {
            int count = stack.stackSize;
            String name = stack.getDisplayName();
            lines.add(Config.hudBlockInventory.formatString(String.format(str, name, count)));
          } catch (Exception ex) {
            // Looks like a mod has done something stupid with their items...
            lines.add(TextFormatting.RED + "<I AM ERROR!>");
          }
        }
      }
      // How many did we not display?
      int omittedCount = invStacks.size() - Config.hudInventoryMaxListCount;
      if (omittedCount > 0) {
        String str2 = LocalizationHelper.instance.get("OmittedInventoryItems");
        lines.add(String.format(str2, omittedCount));
      }
    }
  }

  public List<ItemStack> getInventoryStacks(IInventory inv) {

    List<ItemStack> list = Lists.newArrayList();
    ItemStack stack;
    for (int i = 0; i < inv.getSizeInventory(); ++i) {
      stack = inv.getStackInSlot(i);
      if (stack != null) {
        list.add(stack);
      }
    }
    return list;
  }

  public void getLinesForBlockHarvestability(EntityPlayer player, List<String> lines) {

    if (!Config.hudHarvestable.shouldDisplay(player)) {
      return;
    }

    LocalizationHelper loc = LocalizationHelper.instance;

    Block actualBlock = player.worldObj.getBlockState(pos).getBlock();
    boolean canHarvest = meta >= 0 && meta < 16 // Bad metadata check
        && ForgeHooks.canHarvestBlock(actualBlock, player, player.worldObj, pos)
        && state.getBlockHardness(player.worldObj, pos) >= 0;
    String format = canHarvest ? Config.hudHarvestable.formatString("")
        : Config.hudHarvestable.formatString2("");

    String line;
    if (harvestTool != null && !harvestTool.isEmpty() && harvestLevel > -1) {
      // Known tool
      String strTool = loc.get("Tool." + harvestTool);
      String strLevel = harvestLevel > 0 ? loc.get("HarvestLevel", harvestLevel) : "";
      line = loc.get("HarvestWith", strTool) + strLevel;
    } else {
      // Unknown tool or level, but we know whether or not it can be harvested.
      line = loc.get((canHarvest ? "" : "Not") + "Harvestable");
    }
    lines.add(format + line);
  }

  public void getLinesForMobSpawner(List<String> lines) {

    if (tileEntity instanceof TileEntityMobSpawner) {
      TileEntityMobSpawner tile = (TileEntityMobSpawner) tileEntity;
      Entity entity = tile.getSpawnerBaseLogic().getCachedEntity();
      if (entity != null) {
        EntityInfo entityInfo = new EntityInfo(entity);
        lines.add(entityInfo.localizedName);
      }
    }
  }

  public void getLinesForRfEnergyHandler(List<String> lines) {

    // if (info.tileEntity instanceof IEnergyHandler && !(info.block instanceof IWitHudInfo)) {
    // IEnergyHandler tile = (IEnergyHandler) info.tileEntity;
    // int current = tile.getEnergyStored(EnumFacing.UP);
    // int max = tile.getMaxEnergyStored(EnumFacing.UP);
    // String str = LocalizationHelper.instance.get("RFStorage");
    // str = String.format(str, current, max);
    // lines.add(str);
    // }
  }
}
