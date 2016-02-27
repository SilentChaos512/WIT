package net.silentchaos512.wit.info;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.oredict.OreDictionary;
import net.silentchaos512.wit.WIT;
import net.silentchaos512.wit.api.WitBlockReplacements;

public class ItemStackInfo extends ObjectInfo {

  public final ResourceLocation resourceLocation;

  // Item
  public final Item item;
  public final ItemStack stack;

  // Names
  public final String itemName;

  public ItemStackInfo(ItemStack stack) {

    super(WitBlockReplacements.instance.get(stack));

    this.stack = WitBlockReplacements.instance.get(stack);
    item = this.stack.getItem();

    if (item != null) {
      resourceLocation = (ResourceLocation) GameData.getItemRegistry().getNameForObject(item);
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
}
