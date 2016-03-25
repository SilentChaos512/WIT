package net.silentchaos512.wit.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class WitBlockInfoEvent extends WitHudInfoEvent {

  public final BlockPos pos;
  public final IBlockState blockState;

  public WitBlockInfoEvent(EntityPlayer player, boolean advanced, BlockPos pos, IBlockState state) {

    super(player, advanced);
    this.pos = pos;
    this.blockState = state;
  }
}
