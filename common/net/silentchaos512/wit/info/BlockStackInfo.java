package net.silentchaos512.wit.info;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class BlockStackInfo extends ItemStackInfo {

  // Block
  public final IBlockState state;
  public final BlockPos pos;
  public final Block block;
  public final int blockId;
  public final int meta;
  public final TileEntity tileEntity;
  public final String harvestTool;
  public final int harvestLevel;

  public BlockStackInfo(IBlockState state, BlockPos pos) {

    super(new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)));

    Block newBlock = Block.getBlockFromItem(item);
    meta = stack.getItemDamage();
    IBlockState newState = newBlock.getStateFromMeta(meta);

    this.state = newState;
    this.pos = pos;
    block = newBlock;
    blockId = block.getIdFromBlock(block);
//    meta = block.getMetaFromState(state);
    tileEntity = Minecraft.getMinecraft().thePlayer.worldObj.getTileEntity(pos);
    boolean metaIsGood = meta >= 0 && meta < 16;
    harvestTool = metaIsGood ? block.getHarvestTool(newState) : "null";
    harvestLevel = metaIsGood ? block.getHarvestLevel(newState) : -1;
  }
}
