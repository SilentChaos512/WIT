package net.silentchaos512.wit.api;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.silentchaos512.wit.config.Config;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class WitBlockReplacements implements IBlockReplacements {
    private final Map<String, Supplier<ItemStack>> map = new ConcurrentHashMap<>();

    public static WitBlockReplacements INSTANCE;

    public static void init() {
        if (INSTANCE != null) return;

        INSTANCE = new WitBlockReplacements();
        INSTANCE.addForInfestedBlock(Blocks.INFESTED_STONE, Blocks.STONE);
        INSTANCE.addForInfestedBlock(Blocks.INFESTED_COBBLESTONE, Blocks.COBBLESTONE);
        INSTANCE.addForInfestedBlock(Blocks.INFESTED_CHISELED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS);
        INSTANCE.addForInfestedBlock(Blocks.INFESTED_CRACKED_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS);
        INSTANCE.addForInfestedBlock(Blocks.INFESTED_MOSSY_STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS);
        INSTANCE.addForInfestedBlock(Blocks.INFESTED_STONE_BRICKS, Blocks.STONE_BRICKS);
    }

    private void addForInfestedBlock(Block infested, IItemProvider normal) {
        add(infested, () -> new ItemStack(Config.GENERAL.disguiseInfestedBlocks.get() ? normal : infested));
    }

    private static String keyFor(IForgeRegistryEntry<?> blockOrItem) {
        return Objects.requireNonNull(blockOrItem.getRegistryName()).toString();
    }

    private static String keyFor(ItemStack stack) {
        if (stack.isEmpty()) {
            return "minecraft:air";
        }
        return Objects.requireNonNull(stack.getItem().getRegistryName()).toString();
    }

    public void add(IForgeRegistryEntry<?> actualBlockOrItem, Supplier<ItemStack> display) {
        map.put(keyFor(actualBlockOrItem), display);
    }

    public void add(ItemStack actual, Supplier<ItemStack> display) {
        map.put(keyFor(actual), display);
    }

    public ItemStack get(ItemStack actual) {
        String key = keyFor(actual);
        if (map.containsKey(key))
            return map.get(key).get();
        return actual;
    }
}
