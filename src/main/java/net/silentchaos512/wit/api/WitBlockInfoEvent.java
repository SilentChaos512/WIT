package net.silentchaos512.wit.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class WitBlockInfoEvent extends WitHudInfoEvent {
    private final BlockPos pos;
    private final IBlockState state;
    private final TileEntity tileEntity;

    public WitBlockInfoEvent(EntityPlayer player, World world, boolean advanced, BlockPos pos, IBlockState state) {
        super(player, world, advanced);
        this.pos = pos;
        this.state = state;
        this.tileEntity = world.getTileEntity(pos);
    }

    public BlockPos getPos() {
        return pos;
    }

    public IBlockState getState() {
        return state;
    }

    @Nullable
    public TileEntity getTileEntity() {
        return tileEntity;
    }
}
