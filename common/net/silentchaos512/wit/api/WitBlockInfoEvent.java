package net.silentchaos512.wit.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WitBlockInfoEvent extends WitHudInfoEvent {

  public final BlockPos pos;
  public final IBlockState blockState;
  public final TileEntity tileEntity;

  public WitBlockInfoEvent(EntityPlayer player, boolean advanced, BlockPos pos, IBlockState state) {

    super(player, advanced);
    this.pos = pos;
    this.blockState = state;
    this.tileEntity = world.getTileEntity(pos);
  }
}
