package net.silentchaos512.wit.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class WitEntityInfoEvent extends WitHudInfoEvent {
    private final Entity entity;
    private final EntityLivingBase entityLiving;

    public WitEntityInfoEvent(EntityPlayer player, World world, boolean advanced, Entity entity) {
        super(player, world, advanced);
        this.entity = entity;

        if (entity instanceof EntityLivingBase) {
            this.entityLiving = (EntityLivingBase) entity;
        } else {
            this.entityLiving = null;
        }
    }

    public Entity getEntity() {
        return entity;
    }

    @Nullable
    public EntityLivingBase getEntityLiving() {
        return entityLiving;
    }
}
