package net.silentchaos512.wit.config;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ConfigValueTextElement {
    /**
     * Determines when an element should be shown. Replaces a pair of boolean configs from former
     * versions.
     */
    public enum ShowCondition {
        NEVER(p -> false),
        SNEAK_ONLY(Entity::isSneaking),
        ALWAYS(p -> true);

        final Predicate<EntityPlayer> shouldShow;

        ShowCondition(Predicate<EntityPlayer> shouldShow) {
            this.shouldShow = shouldShow;
        }

        private boolean test(EntityPlayer player) {
            return shouldShow.test(player);
        }
    }

    private Supplier<ShowCondition> showCondition;
    private Supplier<TextFormatting> formatPrimary;
    private Supplier<TextFormatting> formatSecondary;

    public static ConfigValueTextElement define(ForgeConfigSpec.Builder builder, String name, String comment, ShowCondition defaultCondition, TextFormatting format1) {
        return define(builder, name, comment, defaultCondition, format1, null);
    }

    public static ConfigValueTextElement define(ForgeConfigSpec.Builder builder, String name, String comment, ShowCondition defaultCondition, TextFormatting format1, @Nullable TextFormatting format2) {
        // fix parameter formatting later
        ConfigValueTextElement result = new ConfigValueTextElement();

        builder.comment(comment);
        builder.push(name);

        result.showCondition = Config.defineEnumWorkaround(builder, "show", defaultCondition);
        result.formatPrimary = Config.defineEnumWorkaround(builder, "format", format1);
        if (format2 != null) {
            result.formatSecondary = Config.defineEnumWorkaround(builder, "format2", format2);
        }

        builder.pop();

        return result;
    }

    public boolean isShownFor(EntityPlayer player) {
        return showCondition.get().test(player);
    }

    /**
     * Format the ITextComponent based on config settings. The Supplier is not called if this
     * element will not be shown.
     *
     * @param player The client player
     * @param text   The text to create and display if it will be shown
     * @return The text formatted with formatPrimary, or an empty Optional if the text should not be
     * displayed.
     */
    public Optional<ITextComponent> format(EntityPlayer player, Supplier<ITextComponent> text) {
        if (!isShownFor(player))
            return Optional.empty();
        return Optional.of(text.get().applyTextStyle(formatPrimary.get()));
    }

    /**
     * Same as {@link #format(EntityPlayer, Supplier)}, but uses formatSecondary. Format the
     * ITextComponent based on config settings. The Supplier is not called if this element will not
     * be shown.
     *
     * @param player The client player
     * @param text   The text to create and display if it will be shown
     * @return The formatted text, or an empty Optional if the text should not be displayed or
     * formatSecondary produces null.
     */
    public Optional<ITextComponent> format2(EntityPlayer player, Supplier<ITextComponent> text) {
        if (!isShownFor(player) || formatSecondary.get() == null)
            return Optional.empty();
        return Optional.of(text.get().applyTextStyle(formatSecondary.get()));
    }

    /**
     * Calls {@link #format(EntityPlayer, Supplier)} if formatCondition is true, or {@link
     * #format2(EntityPlayer, Supplier)} otherwise. Used for harvestability.
     */
    public Optional<ITextComponent> formatEither(EntityPlayer player, Supplier<ITextComponent> text, boolean formatCondition) {
        return formatCondition ? format(player, text) : format2(player, text);
    }
}
