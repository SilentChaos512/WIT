package net.silentchaos512.wit.config;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class ConfigOptionHudElement {
    public static final String CATEGORY_HUD_DISPLAY = "TODO"; // FIXME

    public static final String KEY_SHOW_ELEMENT = "Show Element";
    public static final String KEY_SNEAK_ONLY = "When Sneaking Only";
    public static final String KEY_FORMAT_1 = "Format String 1";
    public static final String KEY_FORMAT_2 = "Format String 2";

    public static final String COMMENT_SHOW_ELEMENT = "Set to false to never show this element.";
    public static final String COMMENT_SNEAK_ONLY = "Only show the element when the player is sneaking, assuming "
            + KEY_SHOW_ELEMENT + " is true.";
    public static final String COMMENT_FORMAT_1 = "Primary formatting of the text. Uses vanilla formatting codes.";
    public static final String COMMENT_FORMAT_2 = "Secondary formatting of the text. Uses vanilla formatting codes.";

    boolean show = true;
    boolean sneakOnly = false;
    String format1;
    String format2;

    public ConfigOptionHudElement(String name, boolean show, boolean sneakOnly, String format1) {
        this(name, show, sneakOnly, format1, "");
    }

    public ConfigOptionHudElement(String name, boolean show, boolean sneakOnly, String format1, String format2) {
//        super(name, CATEGORY_HUD_DISPLAY + "." + name);
        this.show = show;
        this.sneakOnly = sneakOnly;
        this.format1 = format1;
        this.format2 = format2;
    }

//    @Override
//    public ConfigOption loadValue(Configuration c) {
//        c.setCategoryComment(category, comment);
//        show = c.getBoolean(KEY_SHOW_ELEMENT, category, show, COMMENT_SHOW_ELEMENT);
//        sneakOnly = c.getBoolean(KEY_SNEAK_ONLY, category, sneakOnly, COMMENT_SNEAK_ONLY);
//        format1 = c.getString(KEY_FORMAT_1, category, format1, COMMENT_FORMAT_1);
//        if (!format2.isEmpty()) {
//            format2 = c.getString(KEY_FORMAT_2, category, format2, COMMENT_FORMAT_2);
//        }
//        return this;
//    }

    public boolean shouldDisplay(EntityPlayer player) {
        return show && (!sneakOnly || (sneakOnly && player.isSneaking()));

    }

    @Deprecated
    public String formatString(String str) {
        return format1.replaceAll("&", "\u00a7") + str;
    }

    @Deprecated
    public String formatString2(String str) {
        return format2.replaceAll("&", "\u00a7") + str;
    }

    public ITextComponent format() {
        return new TextComponentString(format1);
    }

    public ITextComponent format(String str) {
        return new TextComponentString(format1 + str);
    }

    public ITextComponent format2() {
        return new TextComponentString(format2);
    }

    public ITextComponent format2(String str) {
        return new TextComponentString(format2 + str);
    }

    public ConfigOptionHudElement setComment(String s) {
        // TODO
        return this;
    }
}
