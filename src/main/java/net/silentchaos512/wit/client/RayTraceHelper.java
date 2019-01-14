package net.silentchaos512.wit.client;

import net.minecraft.block.BlockFlowingFluid;
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
    private static IInfoObject previousInfo;
    private static float stickyTimeout = 0;

    private RayTraceHelper() { throw new IllegalAccessError("Utility class"); }

    @Nullable
    public static HudRenderObject getRenderObject(float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        Entity renderViewEntity = mc.getRenderViewEntity();
        if (renderViewEntity == null) return null;

        World world = renderViewEntity.world;

        RayTraceResult rt = mc.objectMouseOver;

        if (rt != null) {
            if (rt.entity != null) {
                // Looking at an entity (longer sticky time)
                IInfoObject info = infoForEntity(rt);
                previousInfo = info;
                stickyTimeout = 80; // TODO: Config for sticky times
                return new HudRenderObject(info);
            } else {
                // Looking at a block?
                IInfoObject info = infoForBlock(rt, world);
                if (info != null) {
                    previousInfo = info;
                    stickyTimeout = 20; // TODO: Config for sticky times
                    return new HudRenderObject(info);
                }
            }
        }

        // Looking at nothing right now, show previous for a short time?
        if (stickyTimeout > 0) {
            stickyTimeout -= partialTicks;
            return new HudRenderObject(previousInfo);
        }

        return null;
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
        ItemStack blockStack = new ItemStack(state.getBlock());

        if (pickBlockStack.isEmpty() && blockStack.isEmpty()) {
            // No information to work with? Water does this.
            if (state.getBlock() instanceof BlockFlowingFluid) {
                // Ignore water/lava. Should we be more specific?
                return null;
            }
            return new ErrorInfo("Block has no item?", state.toString());
        } else if (blockStack.isEmpty()) {
            // Block does not have an item, but we can work with the pick block stack.
            return new ItemStackInfo(pickBlockStack);
        }

        // Handle block normally
        return new BlockStackInfo(world, state, pos);
    }
}
