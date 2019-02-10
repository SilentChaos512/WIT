package net.silentchaos512.wit.config;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.silentchaos512.utils.config.ConfigSpecWrapper;
import net.silentchaos512.utils.config.EnumValue;
import net.silentchaos512.wit.client.key.KeyTracker;

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
        ALT_KEY(p -> KeyTracker.altDown()),
        CTRL_KEY(p -> KeyTracker.ctrlDown()),
        SHIFT_KEY(p -> KeyTracker.shiftDown()),
        ALWAYS(p -> true);

        final Predicate<PlayerEntity> shouldShow;

        ShowCondition(Predicate<PlayerEntity> shouldShow) {
            this.shouldShow = shouldShow;
        }

        private boolean test(PlayerEntity player) {
            return shouldShow.test(player);
        }
    }

    private EnumValue<ShowCondition> showCondition;
    private EnumValue<TextFormat> formatPrimary;
    private EnumValue<TextFormat> formatSecondary;

    public static ConfigValueTextElement define(ConfigSpecWrapper wrapper, String name, String comment, ShowCondition defaultCondition, TextFormat format1) {
        return define(wrapper, name, comment, defaultCondition, format1, null);
    }

    public static ConfigValueTextElement define(ConfigSpecWrapper wrapper, String name, String comment, ShowCondition defaultCondition, TextFormat format1, @Nullable TextFormat format2) {
        ConfigValueTextElement result = new ConfigValueTextElement();
        wrapper.comment(name, comment);

        result.showCondition = wrapper.builder(name + ".show").defineEnum(defaultCondition);
        result.formatPrimary = wrapper.builder(name + ".format").defineEnum(format1);
        if (format2 != null) {
            result.formatSecondary = wrapper.builder(name + ".format2").defineEnum(format2);
        }

        return result;
    }

    public boolean isShownFor(PlayerEntity player) {
        return showCondition.get().test(player);
    }

    /**
     * Format the TextComponent based on config settings. The Supplier is not called if this
     * element will not be shown.
     *
     * @param player The client player
     * @param text   The text to create and display if it will be shown
     * @return The text formatted with formatPrimary, or an empty Optional if the text should not be
     * displayed.
     */
    public Optional<TextComponent> format(PlayerEntity player, Supplier<TextComponent> text) {
        if (!isShownFor(player))
            return Optional.empty();
        return Optional.of(text.get().applyFormat(formatPrimary.get()));
    }

    /**
     * Same as {@link #format(PlayerEntity, Supplier)}, but uses formatSecondary. Format the
     * TextComponent based on config settings. The Supplier is not called if this element will not
     * be shown.
     *
     * @param player The client player
     * @param text   The text to create and display if it will be shown
     * @return The formatted text, or an empty Optional if the text should not be displayed or
     * formatSecondary produces null.
     */
    public Optional<TextComponent> format2(PlayerEntity player, Supplier<TextComponent> text) {
        if (!isShownFor(player) || formatSecondary.get() == null)
            return Optional.empty();
        return Optional.of(text.get().applyFormat(formatSecondary.get()));
    }

    /**
     * Calls {@link #format(PlayerEntity, Supplier)} if formatCondition is true, or {@link
     * #format2(PlayerEntity, Supplier)} otherwise. Used for harvestability.
     */
    public Optional<TextComponent> formatEither(PlayerEntity player, Supplier<TextComponent> text, boolean formatCondition) {
        return formatCondition ? format(player, text) : format2(player, text);
    }
}
