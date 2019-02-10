package net.silentchaos512.wit.api;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class WitBlockInfoEvent extends WitHudInfoEvent {
    private final BlockPos pos;
    private final BlockState state;
    private final BlockEntity tileEntity;

    public WitBlockInfoEvent(PlayerEntity player, World world, boolean advanced, BlockPos pos, BlockState state) {
        super(player, world, advanced);
        this.pos = pos;
        this.state = state;
        this.tileEntity = world.getBlockEntity(pos);
    }

    public BlockPos getPos() {
        return pos;
    }

    public BlockState getState() {
        return state;
    }

    @Nullable
    public BlockEntity getTileEntity() {
        return tileEntity;
    }
}
