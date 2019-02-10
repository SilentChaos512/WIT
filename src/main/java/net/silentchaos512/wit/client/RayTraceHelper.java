package net.silentchaos512.wit.client;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.silentchaos512.wit.api.IInfoObject;
import net.silentchaos512.wit.config.Config;
import net.silentchaos512.wit.info.BlockStackInfo;
import net.silentchaos512.wit.info.EntityInfo;
import net.silentchaos512.wit.info.ErrorInfo;

import javax.annotation.Nullable;

public final class RayTraceHelper {
    private static IInfoObject previousInfo;
    private static double stickyTimeout = 0;

    private RayTraceHelper() { throw new IllegalAccessError("Utility class"); }

    @Nullable
    public static HudRenderObject getRenderObject(float partialTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Entity renderViewEntity = mc.getCameraEntity();
        if (renderViewEntity == null) return null;

        World world = renderViewEntity.world;

        HitResult rt = mc.hitResult;

        if (rt != null) {
            if (rt.getType() == HitResult.Type.ENTITY) {
                // Looking at an entity (longer sticky time)
                IInfoObject info = infoForEntity((EntityHitResult) rt);
                previousInfo = info;
                stickyTimeout = Config.HUD.entityStickyTime.get() * 20;
                return new HudRenderObject(info);
            } else if (rt.getType() == HitResult.Type.BLOCK) {
                // Looking at a block?
                IInfoObject info = infoForBlock((BlockHitResult) rt, world);
                if (info != null) {
                    previousInfo = info;
                    stickyTimeout = Config.HUD.blockStickyTime.get() * 20;
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

    private static IInfoObject infoForEntity(EntityHitResult rt) {
        return new EntityInfo(rt.getEntity());
    }

    private static IInfoObject infoForBlock(BlockHitResult rt, World world) {
        BlockPos pos = rt.getBlockPos();
        BlockState state = world.getBlockState(pos);
        if (state.isAir()) return null;

        // Pick block will return something valid in most cases
        ItemStack pickBlockStack = state.getBlock().getPickStack(world, pos, state);
        ItemStack blockStack = new ItemStack(state.getBlock());

        if (pickBlockStack.isEmpty() && blockStack.isEmpty()) {
            // No information to work with? Water does this.
            if (state.getBlock() instanceof FluidBlock) {
                // Ignore water/lava. Should we be more specific?
                return null;
            }
            return new ErrorInfo("Block has no item?", state.toString());
        } else if (blockStack.isEmpty()) {
            // Block does not have an item, but we can work with the pick block stack.
//            return new ItemStackInfo(pickBlockStack);
            return new BlockStackInfo(pickBlockStack, world, state, pos);
        }

        // Handle block normally
        return new BlockStackInfo(world, state, pos);
    }
}
