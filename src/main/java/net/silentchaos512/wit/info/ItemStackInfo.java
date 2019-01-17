package net.silentchaos512.wit.info;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.wit.api.WitBlockReplacements;
import net.silentchaos512.wit.config.Config;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ItemStackInfo extends ObjectInfo {
    final ItemStack stack;

    public ItemStackInfo(@Nonnull ItemStack stack) {
        super(nameFor(replacementFor(stack)));
        this.stack = replacementFor(stack);
    }

    private static ResourceLocation nameFor(ItemStack stack) {
        ResourceLocation regName = stack.getItem().getRegistryName();
        return regName != null ? regName : new ResourceLocation("unknown_item_error");
    }

    private static ItemStack replacementFor(ItemStack stack) {
        return WitBlockReplacements.INSTANCE.get(stack);
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
    public void addLines(EntityPlayer player, List<ITextComponent> lines) {
        // Name
        Config.HUD.elementName.format(player, this::displayItemName).ifPresent(lines::add);

        // Registry name
        Config.HUD.elementRegistryName.format(player, this::displayRegistryName).ifPresent(lines::add);

        // Mod name
        Config.HUD.elementModName.format(player, this::displayModName).ifPresent(lines::add);
    }

    public final ItemStack getStack() {
        return stack;
    }

    ITextComponent displayItemName() {
//        return stack.getDisplayName().applyTextStyle(stack.getRarity().color);
        return stack.getDisplayName();
    }
}
