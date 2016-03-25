package net.silentchaos512.wit.api;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.world.WorldEvent;

public class WitHudInfoEvent extends WorldEvent {

  public final List<String> lines;
  public final boolean isSneaking;
  public final boolean advanced;
  public final EntityPlayer player;

  public WitHudInfoEvent(EntityPlayer player, boolean advanced) {

    super(player.worldObj);
    this.player = player;
    this.isSneaking = player.isSneaking();
    this.advanced = advanced;
    this.lines = Lists.newArrayList();
  }
}
