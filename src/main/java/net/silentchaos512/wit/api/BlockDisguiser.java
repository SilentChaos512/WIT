package net.silentchaos512.wit.api;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.silentchaos512.wit.config.Config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class BlockDisguiser {
    private static final Map<String, Supplier<ItemStack>> MAP = new ConcurrentHashMap<>();

    static {
        addForInfestedBlock(Blocks.INFESTED_STONE, Blocks.STONE);
        addForInfestedBlock(Blocks.INFESTED_COBBLESTONE, Blocks.COBBLESTONE);
        addForInfestedBlock(Blocks.INFESTED_CHISELED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS);
        addForInfestedBlock(Blocks.INFESTED_CRACKED_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS);
        addForInfestedBlock(Blocks.INFESTED_MOSSY_STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS);
        addForInfestedBlock(Blocks.INFESTED_STONE_BRICKS, Blocks.STONE_BRICKS);
    }

    private BlockDisguiser() {throw new IllegalAccessError("Utility class");}

    private static void addForInfestedBlock(ItemProvider infested, ItemProvider normal) {
        add(infested, () -> new ItemStack(Config.GENERAL.disguiseInfestedBlocks.get() ? normal : infested));
    }

    private static String keyFor(ItemProvider blockOrItem) {
        return blockOrItem.getItem().getTranslationKey();
    }

    private static String keyFor(ItemStack stack) {
        if (stack.isEmpty()) {
            return Items.AIR.getTranslationKey();
        }
        return stack.getItem().getTranslationKey();
    }

    public static void add(ItemProvider actual, Supplier<ItemStack> display) {
        MAP.put(keyFor(actual), display);
    }

    public static void add(ItemStack actual, Supplier<ItemStack> display) {
        MAP.put(keyFor(actual), display);
    }

    public static ItemStack get(ItemStack actual) {
        String key = keyFor(actual);
        if (MAP.containsKey(key))
            return MAP.get(key).get();
        return actual;
    }
}
