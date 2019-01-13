package net.silentchaos512.wit.info;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.silentchaos512.wit.api.IInfoObject;

import java.util.List;

public class ErrorInfo implements IInfoObject {
    @Override
    public void addLines(EntityPlayer player, List<ITextComponent> lines) {
        // TODO: Do anything with this?
        lines.add(new TextComponentString("An unknown error has occurred."));
    }
}
