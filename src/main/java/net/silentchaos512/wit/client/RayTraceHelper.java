package net.silentchaos512.wit.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.silentchaos512.wit.api.IInfoObject;
import net.silentchaos512.wit.info.BlockStackInfo;
import net.silentchaos512.wit.info.EntityInfo;
import net.silentchaos512.wit.info.ErrorInfo;
import net.silentchaos512.wit.info.ItemStackInfo;

import javax.annotation.Nullable;

public final class RayTraceHelper {
    private RayTraceHelper() { throw new IllegalAccessError("Utility class"); }

    @Nullable
    public static HudRenderObject getRenderObject() {
        Minecraft mc = Minecraft.getInstance();
        Entity renderViewEntity = mc.getRenderViewEntity();
        if (renderViewEntity == null) return null;

        World world = renderViewEntity.world;

        RayTraceResult rt = mc.objectMouseOver;
        if (rt == null) return null;

        if (rt.entity != null) {
            return from(infoForEntity(rt));
        } else {
            return from(infoForBlock(rt, world));
        }
    }

    @Nullable
    private static HudRenderObject from(IInfoObject info) {
        return info != null ? new HudRenderObject(info) : null;
    }

    private static IInfoObject infoForEntity(RayTraceResult rt) {
        return new EntityInfo(rt.entity);
    }

    private static IInfoObject infoForBlock(RayTraceResult rt, World world) {
        BlockPos pos = rt.getBlockPos();
        IBlockState state = world.getBlockState(pos);
        if (state.isAir(world, pos)) return null;

        // Pick block will return something valid in most cases
        ItemStack pickBlockStack = state.getPickBlock(rt, world, pos, Minecraft.getInstance().player);
        if (pickBlockStack.isEmpty()) {
            // Empty pick block, very rare
            return new ErrorInfo("ItemStack is empty");
        } else if (new ItemStack(state.getBlock()).isEmpty()) {
            // Block does not have an item, but we can work with the pick block stack.
            return new ItemStackInfo(pickBlockStack);
        }

        // Handle block normally
        return new BlockStackInfo(world, state, pos);
    }
}
