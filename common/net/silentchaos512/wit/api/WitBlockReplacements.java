package net.silentchaos512.wit.api;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class WitBlockReplacements {

  private Map<Integer, ItemStack> map = new HashMap<Integer, ItemStack>();

  public static WitBlockReplacements instance;

  public static void init() {

    instance = new WitBlockReplacements();
  }

  private int getKeyFromStack(ItemStack stack) {

    if (stack == null || stack.getItem() == null) {
      return -1;
    }
    return Item.getIdFromItem(stack.getItem()) + (stack.getItemDamage() << 16);
  }

  public void add(ItemStack actualStack, ItemStack displayStack) {

    map.put(getKeyFromStack(actualStack), displayStack);
  }

  public ItemStack get(ItemStack actualStack) {

    int key = getKeyFromStack(actualStack);
    return map.containsKey(key) ? map.get(key) : actualStack;
  }
}
