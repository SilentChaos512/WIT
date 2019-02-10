package net.silentchaos512.wit.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class WitHudInfoEvent /*extends WorldEvent*/ {
    private final List<TextComponent> lines;
    private final PlayerEntity player;
    private final boolean isSneaking;
    private final boolean advanced;

    WitHudInfoEvent(PlayerEntity player, World world, boolean advanced) {
//        super(world);
        this.player = player;
        this.isSneaking = this.player.isSneaking();
        this.advanced = advanced;
        this.lines = new ArrayList<>();
    }

    public void add(TextComponent line) {
        this.lines.add(line);
    }

    public void add(String lineOfRawText) {
        this.lines.add(new StringTextComponent(lineOfRawText));
    }

    public void add(String translationKey, Object... formatArgs) {
        this.lines.add(new TranslatableTextComponent(translationKey, formatArgs));
    }

    public List<TextComponent> getLines() {
        // TODO: Return a copy?
        return lines;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public boolean isAdvanced() {
        return advanced;
    }

    public boolean isSneaking() {
        return isSneaking;
    }
}
