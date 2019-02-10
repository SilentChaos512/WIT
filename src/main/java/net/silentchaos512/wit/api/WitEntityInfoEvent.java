package net.silentchaos512.wit.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class WitEntityInfoEvent extends WitHudInfoEvent {
    private final Entity entity;
    private final LivingEntity entityLiving;

    public WitEntityInfoEvent(PlayerEntity player, World world, boolean advanced, Entity entity) {
        super(player, world, advanced);
        this.entity = entity;

        if (entity instanceof LivingEntity) {
            this.entityLiving = (LivingEntity) entity;
        } else {
            this.entityLiving = null;
        }
    }

    public Entity getEntity() {
        return entity;
    }

    @Nullable
    public LivingEntity getLivingEntity() {
        return entityLiving;
    }
}
