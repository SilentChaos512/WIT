package net.silentchaos512.wit.api;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.silentchaos512.wit.config.Config;

import java.util.Map;
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

    private void addForInfestedBlock(ItemProvider infested, ItemProvider normal) {
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

    public void add(ItemProvider actual, Supplier<ItemStack> display) {
        map.put(keyFor(actual), display);
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
