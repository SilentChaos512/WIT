package net.silentchaos512.wit.info;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration;
import net.silentchaos512.wit.WIT;
import net.silentchaos512.wit.api.IInfoObject;

public class ObjectInfo implements IInfoObject {

  // Names
  String unlocalizedName;
  String localizedName;

  // Mod
  protected String modId;
  protected String modName;
  protected ModContainer mod;

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
    this.localizedName = I18n.format(unlocalizedName + ".name");
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

    String entityString = EntityList.getEntityString(entity);
    this.unlocalizedName = entityString == null || entityString.isEmpty() ? "unknown"
        : entityString;

    ITextComponent dispName = entity.getDisplayName();
    if (dispName != null && !dispName.getUnformattedComponentText().isEmpty())
      this.localizedName = dispName.getUnformattedComponentText();
    else if (entity.getName() != null && !entity.getName().isEmpty())
      this.localizedName = entity.getName();
    else
      this.localizedName = unlocalizedName;
  }

  public ObjectInfo(Entity entity) {

    this(getModFromEntity(entity), entity);
  }

  public ObjectInfo(@Nonnull ItemStack stack) {

    this(getModFromItem(stack), stack.getItem().getUnlocalizedName(stack));

    if (localizedName.equals(unlocalizedName + ".name")) {
      localizedName = stack.getDisplayName();
    }
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

  public static ModContainer getModFromItem(ItemStack stack) {

    if (stack == null) {
      return null;
    }

    Item item = stack.getItem();
    if (item == null) {
      return null;
    }

    ResourceLocation location = item.getRegistryName();
    return WIT.instance.mods.get(location.getResourceDomain());
  }

  @Override
  public void addLines(EntityPlayer player, List<String> lines) {

    // TODO Auto-generated method stub
  }

  public String getUnlocalizedName() {

    return unlocalizedName;
  }

  public String getLocalizedName() {

    return localizedName;
  }

  public String getModId() {

    return modId;
  }

  public String getModName() {

    return modName;
  }

  public ModContainer getMod() {

    return mod;
  }
}
