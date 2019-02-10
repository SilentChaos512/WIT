package net.silentchaos512.wit.lib;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.tag.FabricItemTags;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tag.Tag;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Temporary hack for getting harvest levels. It's as gross as it looks.
 */
public final class MiningLevelHelper {
    private static final Map<Tag<Item>, List<Item>> TOOLS = ImmutableMap.<Tag<Item>, List<Item>>builder()
            .put(FabricItemTags.AXES, ImmutableList.of(Items.WOODEN_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.DIAMOND_AXE))
            .put(FabricItemTags.PICKAXES, ImmutableList.of(Items.WOODEN_PICKAXE, Items.STONE_PICKAXE, Items.IRON_PICKAXE, Items.DIAMOND_PICKAXE))
            .put(FabricItemTags.SHOVELS, ImmutableList.of(Items.WOODEN_SHOVEL, Items.STONE_SHOVEL, Items.IRON_SHOVEL, Items.DIAMOND_SHOVEL))
            .put(FabricItemTags.SWORDS, ImmutableList.of(Items.WOODEN_HOE, Items.STONE_HOE, Items.IRON_HOE, Items.DIAMOND_HOE))
            .build();

    private MiningLevelHelper() {}

    @Nullable
    public static Tag<Item> getHarvestTool(BlockState state) {
        if (Items.DIAMOND_PICKAXE.isEffectiveOn(state))
            return FabricItemTags.PICKAXES;
        if (Items.DIAMOND_SHOVEL.isEffectiveOn(state))
            return FabricItemTags.SHOVELS;
        if (Items.DIAMOND_AXE.isEffectiveOn(state))
            return FabricItemTags.AXES;
        if (Items.DIAMOND_SWORD.isEffectiveOn(state))
            return FabricItemTags.SWORDS;
        if (Items.DIAMOND_HOE.isEffectiveOn(state))
            return FabricItemTags.HOES;
        return null;
    }

    public static int getLevel(Tag<Item> toolType, BlockState state) {
        if (!TOOLS.containsKey(toolType)) return -1;
        List<Item> tools = TOOLS.get(toolType);
        for (int i = 0; i < tools.size(); ++i) {
            if (tools.get(i).isEffectiveOn(state)) {
                return i;
            }
        }
        return -1;
    }
}
