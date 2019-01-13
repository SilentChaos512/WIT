package net.silentchaos512.wit.api;

import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WitBlockReplacements implements IBlockReplacements {
    private final Map<String, ItemStack> map = new HashMap<>();

    public static WitBlockReplacements INSTANCE;

    public static void init() {
        INSTANCE = new WitBlockReplacements();
    }

    private static String getKeyFromStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return "minecraft:air";
        }
        // TODO: Is this sufficient?
        return Objects.requireNonNull(stack.getItem().getRegistryName()).toString();
    }

    public void add(ItemStack actualStack, ItemStack displayStack) {
        map.put(getKeyFromStack(actualStack), displayStack);
    }

    public ItemStack get(ItemStack actualStack) {
        return map.getOrDefault(getKeyFromStack(actualStack), actualStack);
    }
}
