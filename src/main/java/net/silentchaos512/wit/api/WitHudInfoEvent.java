package net.silentchaos512.wit.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.List;

public class WitHudInfoEvent<I extends IInfoObject> {
    private final List<ITextComponent> lines;
    private final EntityPlayer player;
    private final World world;
    private final I info;
    private final IInfoOptions options;

    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    public WitHudInfoEvent(EntityPlayer player, World world, I info, List<ITextComponent> lines, IInfoOptions options) {
        this.player = player;
        this.world = world;
        this.info = info;
        this.options = options;
        this.lines = lines;
    }

    public void add(ITextComponent line) {
        this.lines.add(line);
    }

    public void add(String lineOfRawText) {
        this.lines.add(new TextComponentString(lineOfRawText));
    }

    public void add(String translationKey, Object... formatArgs) {
        this.lines.add(new TextComponentTranslation(translationKey, formatArgs));
    }

    @SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
    public List<ITextComponent> getLines() {
        return lines;
    }

    public I getInfo() {
        return info;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public World getWorld() {
        return world;
    }

    public IInfoOptions getOptions() {
        return options;
    }
}
