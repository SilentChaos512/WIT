package net.silentchaos512.wit.api;

import net.silentchaos512.wit.info.BlockStackInfo;
import net.silentchaos512.wit.info.EntityInfo;
import net.silentchaos512.wit.info.ItemStackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class InfoCallbacks {
    private static final List<Consumer<WitHudInfoEvent<BlockStackInfo>>> BLOCKS = new ArrayList<>();
    private static final List<Consumer<WitHudInfoEvent<ItemStackInfo>>> ITEMS = new ArrayList<>();
    private static final List<Consumer<WitHudInfoEvent<EntityInfo>>> ENTITIES = new ArrayList<>();

    private InfoCallbacks() {}

    public static void registerBlockHandler(Consumer<WitHudInfoEvent<BlockStackInfo>> callback) {
        BLOCKS.add(callback);
    }

    public static void registerItemHandler(Consumer<WitHudInfoEvent<ItemStackInfo>> callback) {
        ITEMS.add(callback);
    }

    public static void registerEntityHandler(Consumer<WitHudInfoEvent<EntityInfo>> callback) {
        ENTITIES.add(callback);
    }

    public static void postBlockInfo(WitHudInfoEvent<BlockStackInfo> event) {
        BLOCKS.forEach(c -> c.accept(event));
    }

    public static void postItemInfo(WitHudInfoEvent<ItemStackInfo> event) {
        ITEMS.forEach(c -> c.accept(event));
    }

    public static void postEntityInfo(WitHudInfoEvent<EntityInfo> event) {
        ENTITIES.forEach(c -> c.accept(event));
    }
}
