package net.silentchaos512.wit.info;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.silentchaos512.wit.api.BlockDisguiser;
import net.silentchaos512.wit.api.InfoCallbacks;
import net.silentchaos512.wit.api.WitHudInfoEvent;
import net.silentchaos512.wit.config.Config;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ItemStackInfo extends ObjectInfo {
    private final ItemStack stack;

    public ItemStackInfo(@Nonnull ItemStack stack) {
        super(nameFor(replacementFor(stack)));
        this.stack = replacementFor(stack);
    }

    private static Identifier nameFor(ItemStack stack) {
        return Registry.ITEM.getId(stack.getItem());
    }

    private static ItemStack replacementFor(ItemStack stack) {
        return BlockDisguiser.get(stack);
    }

    public List<String> getOreNames() {
        List<String> result = new ArrayList<>();
        // TODO: Vanilla tags?
//        if (!stack.isEmpty()) {
//            for (int id : OreDictionary.getOreIDs(stack)) {
//                result.add(OreDictionary.getOreName(id));
//            }
//        }
        return result;
    }

    @Override
    public void addLines(PlayerEntity player, List<TextComponent> lines) {
        // Name
        Config.HUD.elementName.format(player, this::displayItemName).ifPresent(lines::add);

        // Registry name
        Config.HUD.elementRegistryName.format(player, this::displayRegistryName).ifPresent(lines::add);

        // Info Event
        InfoCallbacks.postItemInfo(new WitHudInfoEvent<>(player, player.world, this, lines, Config.GENERAL.advancedMode::get));

        // Mod name
        Config.HUD.elementModName.format(player, this::displayModName).ifPresent(lines::add);
    }

    public final ItemStack getStack() {
        return stack;
    }

    TextComponent displayItemName() {
        return stack.getDisplayName();
    }
}
