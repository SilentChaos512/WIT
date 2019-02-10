package net.silentchaos512.wit.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TextComponent;

import java.util.List;

public interface IInfoObject {
    void addLines(PlayerEntity player, List<TextComponent> lines);
}
