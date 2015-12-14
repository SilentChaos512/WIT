package net.silentchaos512.wit.info;

import net.minecraft.entity.Entity;

public class EntityInfo extends ObjectInfo {

  public final Entity entity;

  public EntityInfo(Entity entity) {

    super(entity);
    this.entity = entity;
  }
}
