package net.silentchaos512.wit.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.World;

import java.util.List;

public class WitHudInfoEvent<I extends IInfoObject> {
    private final List<TextComponent> lines;
    private final PlayerEntity player;
    private final World world;
    private final I info;
    private final IInfoOptions options;

    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    public WitHudInfoEvent(PlayerEntity player, World world, I info, List<TextComponent> lines, IInfoOptions options) {
        this.player = player;
        this.world = world;
        this.info = info;
        this.options = options;
        this.lines = lines;
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

    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    public List<TextComponent> getLines() {
        return lines;
    }

    public I getInfo() {
        return info;
    }

    public PlayerEntity getPlayer() {
        return player;
    }

    public World getWorld() {
        return world;
    }

    public IInfoOptions getOptions() {
        return options;
    }
}
