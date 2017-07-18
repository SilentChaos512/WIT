package net.silentchaos512.wit.info;

import java.util.List;

import javax.annotation.Nonnull;

import lombok.AccessLevel;
import lombok.Getter;
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
  @Getter(value = AccessLevel.PUBLIC)
  String unlocalizedName;
  @Getter(value = AccessLevel.PUBLIC)
  String localizedName;
  protected String nameException = "";

  // Mod
  @Getter(value = AccessLevel.PUBLIC)
  protected String modId;
  @Getter(value = AccessLevel.PUBLIC)
  protected String modName;
  @Getter(value = AccessLevel.PUBLIC)
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
    this.nameException = lastNameException;
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

    this(getModFromItem(stack), safeGetUnlocalizedName(stack));

    if (localizedName.equals(unlocalizedName + ".name")) {
      try {
        localizedName = stack.getDisplayName();
        lastNameException = "";
      } catch (Exception ex) {
        lastNameException = ex.getClass().getName();
        localizedName = "Broken Name";
      }
    }
  }

  private static String lastNameException = "";

  protected static String safeGetUnlocalizedName(@Nonnull ItemStack stack) {

    try {
      String str = stack.getItem().getUnlocalizedName(stack);
      lastNameException = "";
      return str;
    } catch (Exception ex) {
      lastNameException = ex.getClass().getName();
      return "Broken Name";
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
}
