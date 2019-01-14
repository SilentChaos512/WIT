package net.silentchaos512.wit.info;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.silentchaos512.wit.api.WitBlockReplacements;
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
        if (Config.hudObjectName.shouldDisplay(player)) {
            lines.add(this.displayItemName());
        }

        // Registry name
        if (Config.hudResourceName.shouldDisplay(player)) {
            lines.add(this.displayRegistryName());
        }

        // Mod name
        if (Config.hudModName.shouldDisplay(player)) {
            lines.add(this.displayModName());
        }
    }

    public final ItemStack getStack() {
        return stack;
    }

    ITextComponent displayItemName() {
        return new TextComponentString(String.valueOf(stack.getRarity().color))
                .appendSibling(stack.getDisplayName());
    }
}
