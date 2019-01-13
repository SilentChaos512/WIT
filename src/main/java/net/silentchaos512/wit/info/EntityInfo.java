package net.silentchaos512.wit.info;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.wit.api.WitEntityInfoEvent;
import net.silentchaos512.wit.config.Config;

import java.util.List;
import java.util.Objects;

public class EntityInfo extends ObjectInfo {
    protected Entity entity;

    public EntityInfo(Entity entity) {
        super(nameFor(entity));
        this.entity = entity;
    }

    private static ResourceLocation nameFor(Entity entity) {
        return Objects.requireNonNull(ForgeRegistries.ENTITIES.getKey(entity.getType()));
    }

    @Override
    public void addLines(EntityPlayer player, List<ITextComponent> lines) {
        // Entity name
        if (Config.hudObjectName.shouldDisplay(player)) {
            lines.add(this.displayEntityName());
        }

        // Health
        if (entity instanceof EntityLiving) {
            EntityLiving entityLiving = (EntityLiving) entity;
            if (Config.hudEntityHealth.shouldDisplay(player)) {
                String current = String.format("%.1f", entityLiving.getHealth());
                String max = String.format("%.1f", entityLiving.getMaxHealth());
                lines.add(new TextComponentTranslation("hud.wit.entity.health", current, max));
            }
        }

        // TODO: Armor?

        // Registry name
        if (Config.hudResourceName.shouldDisplay(player)) {
            lines.add(this.displayRegistryName());
        }

        // WIT HUD info event
        processInfoEvent(lines, new WitEntityInfoEvent(player, entity.world, Config.hudAdvancedMode, entity));

        // Mod name
        if (Config.hudModName.shouldDisplay(player)) {
            lines.add(this.displayModName());
        }
    }

    private ITextComponent displayEntityName() {
        return this.entity.getDisplayName();
    }
}
