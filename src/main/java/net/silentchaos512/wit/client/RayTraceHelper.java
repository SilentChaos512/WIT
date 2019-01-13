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
            return new HudRenderObject(forEntity(rt));
        } else {
            return new HudRenderObject(forBlock(rt, world));
        }
    }

    private static IInfoObject forEntity(RayTraceResult rt) {
        return new EntityInfo(rt.entity);
    }

    private static IInfoObject forBlock(RayTraceResult rt, World world) {
        BlockPos pos = rt.getBlockPos();
        IBlockState state = world.getBlockState(pos);
        if (state.isAir(world, pos)) return null;

        ItemStack stack = state.getPickBlock(rt, world, pos, Minecraft.getInstance().player);
        if (stack.isEmpty()) {
            // TODO: How to handle this?
            return new ErrorInfo();
        }

        return new BlockStackInfo(world, state, pos);
    }
}
