package net.silentchaos512.wit.info;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.silentchaos512.wit.api.IInfoObject;

import java.util.List;

public class ErrorInfo implements IInfoObject {
    private final String msg;

    public ErrorInfo() {
        this.msg = "An unknown error has occurred";
    }

    public ErrorInfo(String msg) {
        this.msg = msg;
    }

    @Override
    public void addLines(EntityPlayer player, List<ITextComponent> lines) {
        lines.add(new TextComponentString("Error: " + this.msg));
    }
}
