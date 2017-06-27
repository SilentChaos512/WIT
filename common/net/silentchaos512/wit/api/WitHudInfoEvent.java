package net.silentchaos512.wit.api;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

public class WitHudInfoEvent extends WorldEvent {

  public final List<String> lines;
  public final boolean isSneaking;
  public final boolean advanced;
  public final EntityPlayer player;
  public final World world;

  public WitHudInfoEvent(EntityPlayer player, boolean advanced) {

    super(player.world);
    this.player = player;
    this.world = player.world;
    this.isSneaking = player.isSneaking();
    this.advanced = advanced;
    this.lines = Lists.newArrayList();
  }
}
