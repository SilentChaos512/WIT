package net.silentchaos512.wit.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

import java.util.ArrayList;
import java.util.List;

public class WitHudInfoEvent extends WorldEvent {
    private final List<ITextComponent> lines;
    private final EntityPlayer player;
    private final boolean isSneaking;
    private final boolean advanced;

    WitHudInfoEvent(EntityPlayer player, World world, boolean advanced) {
        super(world);
        this.player = player;
        this.isSneaking = this.player.isSneaking();
        this.advanced = advanced;
        this.lines = new ArrayList<>();
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

    public List<ITextComponent> getLines() {
        // TODO: Return a copy?
        return lines;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public boolean isAdvanced() {
        return advanced;
    }

    public boolean isSneaking() {
        return isSneaking;
    }
}
