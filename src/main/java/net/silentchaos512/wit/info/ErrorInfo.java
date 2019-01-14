package net.silentchaos512.wit.info;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.silentchaos512.wit.api.IInfoObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ErrorInfo implements IInfoObject {
    private final List<String> msgLines = new ArrayList<>();

    public ErrorInfo(String line) {
        this.msgLines.add(line);
    }

    public ErrorInfo(String... lines) {
        Collections.addAll(this.msgLines, lines);
    }

    @Override
    public void addLines(EntityPlayer player, List<ITextComponent> lines) {
        this.msgLines.forEach(s -> lines.add(new TextComponentString(s)));
    }
}
