package net.silentchaos512.wit.info;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.silentchaos512.wit.api.IInfoObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ErrorInfo implements IInfoObject {
    private final Collection<String> msgLines = new ArrayList<>();

    public ErrorInfo(String line) {
        this.msgLines.add(line);
    }

    public ErrorInfo(String... lines) {
        Collections.addAll(this.msgLines, lines);
    }

    @Override
    public void addLines(PlayerEntity player, List<TextComponent> lines) {
        this.msgLines.forEach(s -> lines.add(new StringTextComponent(s)));
    }
}
