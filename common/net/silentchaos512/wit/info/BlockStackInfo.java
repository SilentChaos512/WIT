package net.silentchaos512.wit.info;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;

public class BlockStackInfo extends ItemStackInfo {

  // Block
  public final IBlockState state;
  public final Block block;
  public final int blockId;
  public final int meta;
  public final TileEntity tileEntity;

  public BlockStackInfo(IBlockState state, BlockPos pos) {

    super(new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)));

    this.state = state;
    block = state.getBlock();
    blockId = block.getIdFromBlock(block);
    meta = block.getMetaFromState(state);
    tileEntity = Minecraft.getMinecraft().thePlayer.worldObj.getTileEntity(pos);
  }
}
