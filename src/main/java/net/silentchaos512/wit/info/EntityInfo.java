package net.silentchaos512.wit.info;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.wit.api.InfoCallbacks;
import net.silentchaos512.wit.api.WitHudInfoEvent;
import net.silentchaos512.wit.config.Config;

import java.util.List;
import java.util.function.Supplier;

public class EntityInfo extends ObjectInfo {
    protected Entity entity;

    public EntityInfo(Entity entity) {
        super(nameFor(entity));
        this.entity = entity;
    }

    private static ResourceLocation nameFor(Entity entity) {
        ResourceLocation regName = ForgeRegistries.ENTITIES.getKey(entity.getType());
        return regName != null ? regName : new ResourceLocation("unknown_entity_error");
    }

    @Override
    public void addLines(EntityPlayer player, List<ITextComponent> lines) {
        // Entity name
        Config.HUD.elementName.format(player, this::displayEntityName).ifPresent(lines::add);

        // Health and armor
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLiving = (EntityLivingBase) entity;
            showHealth(player, entityLiving, lines);

            int armorValue = entityLiving.getTotalArmorValue();
            if (armorValue > 0) {
                showArmor(player, entityLiving, armorValue, lines);
            }
        }

        // Registry name
        Config.HUD.elementRegistryName.format(player, this::displayRegistryName).ifPresent(lines::add);

        // Wit HUD info event
        InfoCallbacks.postEntityInfo(new WitHudInfoEvent<>(player, player.world, this, lines, Config.GENERAL.advancedMode::get));

        // Mod name
        Config.HUD.elementModName.format(player, this::displayModName).ifPresent(lines::add);
    }

    private ITextComponent displayEntityName() {
        return entity.getDisplayName();
    }

    private static void showHealth(EntityPlayer player, EntityLivingBase entity, List<ITextComponent> lines) {
        if (!Config.HUD.elementEntityHealth.isShownFor(player)) return;

        String current = String.format("%.1f", entity.getHealth());
        String max = String.format("%.1f", entity.getMaxHealth());
        Supplier<ITextComponent> text = () -> new TextComponentTranslation("hud.wit.entity.health", current, max);
        Config.HUD.elementEntityHealth.format(player, text).ifPresent(lines::add);
    }

    private static void showArmor(EntityPlayer player, EntityLivingBase entity, int armorValue, List<ITextComponent> lines) {
        if (!Config.HUD.elementEntityArmor.isShownFor(player)) return;

        Supplier<ITextComponent> text = () -> new TextComponentTranslation("hud.wit.entity.armor", armorValue);
        Config.HUD.elementEntityArmor.format(player, text).ifPresent(lines::add);
    }
}
