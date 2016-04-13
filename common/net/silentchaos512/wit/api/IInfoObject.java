package net.silentchaos512.wit.api;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

public interface IInfoObject {

  public void addLines(EntityPlayer player, List<String> list);
}
