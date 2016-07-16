package net.silentchaos512.wit.info;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.oredict.OreDictionary;
import net.silentchaos512.wit.api.WitBlockReplacements;
import net.silentchaos512.wit.config.Config;

public class ItemStackInfo extends ObjectInfo {

  protected ResourceLocation resourceLocation;

  // Item
  @Getter(value = AccessLevel.PUBLIC)
  Item item;
  @Getter(value = AccessLevel.PUBLIC)
  ItemStack stack;

  // Names
  @Getter(value = AccessLevel.PUBLIC)
  String itemName;

  public ItemStackInfo(@Nonnull ItemStack stack) {

    super(WitBlockReplacements.instance.get(stack));

    this.stack = WitBlockReplacements.instance.get(stack);
    item = this.stack.getItem();

    if (item != null) {
      resourceLocation = Item.REGISTRY.getNameForObject(item);
      itemName = resourceLocation.getResourcePath();
    } else {
      resourceLocation = null;
      itemName = "null";
    }
  }

  public List<String> getOreNames() {

    List<String> result = Lists.newArrayList();
    for (int id : OreDictionary.getOreIDs(stack)) {
      result.add(OreDictionary.getOreName(id));
    }
    return result;
  }

  @Override
  public void addLines(EntityPlayer player, List<String> lines) {

    // Name, ID, meta, tile entity
    String line = Config.hudObjectName.shouldDisplay(player)
        ? item.getRarity(stack).rarityColor + localizedName : "";
    line += Config.hudIdMeta.shouldDisplay(player) ? Config.hudIdMeta
        .formatString(" [" + Item.getIdFromItem(item) + ":" + stack.getItemDamage() + "]") : "";
    lines.add(line);

    // Full (resource) name
    if (Config.hudResourceName.shouldDisplay(player)) {
      lines.add(
          Config.hudResourceName.formatString(modId + ":" + resourceLocation.getResourcePath()));
    }

    // Mod name
    if (Config.hudModName.shouldDisplay(player)) {
      lines.add(Config.hudModName.formatString(modName));
    }
  }
}
