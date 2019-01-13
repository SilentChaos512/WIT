package net.silentchaos512.wit.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public interface IInfoObject {
    void addLines(EntityPlayer player, List<ITextComponent> lines);
}
