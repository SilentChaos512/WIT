package net.silentchaos512.wit.info;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration;
import net.minecraftforge.fml.common.registry.GameData;
import net.silentchaos512.wit.WIT;

public class ObjectInfo {

  // Names
  public final String unlocalizedName;
  public final String localizedName;

  // Mod
  public final String modId;
  public final String modName;
  public final ModContainer mod;

  public ObjectInfo(ModContainer mod, String unlocalizedName) {

    this.mod = mod;
    if (mod == null) {
      this.modId = "minecraft";
      this.modName = "Minecraft";
    } else {
      this.modId = mod.getModId();
      this.modName = mod.getName();
    }

    this.unlocalizedName = unlocalizedName;
    this.localizedName = StatCollector.translateToLocal(unlocalizedName + ".name");
  }
  
  public ObjectInfo(ModContainer mod, Entity entity) {

    this.mod = mod;
    if (mod == null) {
      this.modId = "minecraft";
      this.modName = "Minecraft";
    } else {
      this.modId = mod.getModId();
      this.modName = mod.getName();
    }

    this.unlocalizedName = EntityList.getEntityString(entity);
    this.localizedName = entity.getDisplayName().getFormattedText();
  }

  public ObjectInfo(Entity entity) {

    this(getModFromEntity(entity), entity);
  }

  public ObjectInfo(ItemStack stack) {

    this(getModFromItem(stack.getItem()), stack.getItem().getUnlocalizedName(stack));
  }

  public static ModContainer getModFromEntity(Entity entity) {

    if (entity == null) {
      return null;
    }

    EntityRegistration reg = EntityRegistry.instance().lookupModSpawn(entity.getClass(), false);
    if (reg != null) {
      return reg.getContainer();
    }

    return null;
  }

  public static ModContainer getModFromItem(Item item) {

    if (item == null) {
      return null;
    }

    ResourceLocation location = (ResourceLocation) GameData.getItemRegistry()
        .getNameForObject(item);
    return WIT.instance.mods.get(location.getResourceDomain());
  }
}
