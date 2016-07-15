package net.silentchaos512.wit.info;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.silentchaos512.wit.api.WitEntityInfoEvent;
import net.silentchaos512.wit.config.Config;
import net.silentchaos512.wit.lib.LocalizationHelper;

public class EntityInfo extends ObjectInfo {

  public final Entity entity;

  public EntityInfo(Entity entity) {

    super(entity);
    this.entity = entity;
  }

  @Override
  public void addLines(EntityPlayer player, List<String> lines) {

    String line;

    // Entity name
    line = Config.hudObjectName.shouldDisplay(player) ? localizedName : "";
    line += Config.hudIdMeta.shouldDisplay(player)
        ? Config.hudIdMeta.formatString(" [" + EntityList.getEntityID(entity) + "]") : "";
    lines.add(line);

    // Health
    if (entity instanceof EntityLiving) {
      EntityLiving entityLiving = (EntityLiving) entity;
      if (Config.hudEntityHealth.shouldDisplay(player)) {
        String current = String.format("%.1f", entityLiving.getHealth());
        String max = String.format("%.1f", entityLiving.getMaxHealth());
        lines.add(LocalizationHelper.instance.get("EntityHealth", current, max));
      }
    }

    // Full name
    if (Config.hudResourceName.shouldDisplay(player)) {
      lines.add(Config.hudResourceName.formatString(unlocalizedName));
    }

    // WIT HUD info event
    WitEntityInfoEvent event = new WitEntityInfoEvent(player, Config.hudAdvancedMode, entity);
    if (!MinecraftForge.EVENT_BUS.post(event)) {
      lines.addAll(event.lines);
    }

    // Mod name
    if (Config.hudModName.shouldDisplay(player)) {
      lines.add(Config.hudModName.formatString(modName));
    }
  }
}
