package net.silentchaos512.wit.info;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.silentchaos512.wit.api.WitEntityInfoEvent;
import net.silentchaos512.wit.config.Config;

import java.util.List;
import java.util.function.Supplier;

public class EntityInfo extends ObjectInfo {
    protected Entity entity;

    public EntityInfo(Entity entity) {
        super(nameFor(entity));
        this.entity = entity;
    }

    private static Identifier nameFor(Entity entity) {
        Identifier regName = Registry.ENTITY_TYPE.getId(entity.getType());
        return regName != null ? regName : new Identifier("unknown_entity_error");
    }

    @Override
    public void addLines(PlayerEntity player, List<TextComponent> lines) {
        // Entity name
        Config.HUD.elementName.format(player, this::displayEntityName).ifPresent(lines::add);

        // Health
        if (entity instanceof LivingEntity) {
            LivingEntity entityLiving = (LivingEntity) entity;
            showHealth(player, entityLiving, lines);

            // TODO: Armor?
        }

        // Registry name
        Config.HUD.elementRegistryName.format(player, this::displayRegistryName).ifPresent(lines::add);

        // Wit HUD info event
        processInfoEvent(lines, new WitEntityInfoEvent(player, entity.world, Config.GENERAL.advancedMode.get(), entity));

        // Mod name
        Config.HUD.elementModName.format(player, this::displayModName).ifPresent(lines::add);
    }

    private TextComponent displayEntityName() {
        return entity.getDisplayName();
    }

    private static void showHealth(PlayerEntity player, LivingEntity entity, List<TextComponent> lines) {
        if (!Config.HUD.elementEntityHealth.isShownFor(player)) return;

        String current = String.format("%.1f", entity.getHealth());
        String max = String.format("%.1f", entity.getHealthMaximum());
        Supplier<TextComponent> text = () -> new TranslatableTextComponent("hud.wit.entity.health", current, max);
        Config.HUD.elementEntityHealth.format(player, text).ifPresent(lines::add);
    }
}
