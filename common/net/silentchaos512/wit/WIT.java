package net.silentchaos512.wit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.silentchaos512.wit.api.WitBlockReplacements;
import net.silentchaos512.wit.client.HudRenderObject;
import net.silentchaos512.wit.client.key.KeyTracker;
import net.silentchaos512.wit.compat.multipart.MCMultiPartHandler;
import net.silentchaos512.wit.config.Config;
import net.silentchaos512.wit.info.BlockStackInfo;
import net.silentchaos512.wit.info.EntityInfo;
import net.silentchaos512.wit.info.ItemStackInfo;
import net.silentchaos512.wit.lib.LocalizationHelper;
import net.silentchaos512.wit.proxy.CommonProxy;

//@formatter:off
@Mod(modid = WIT.MOD_ID,
    name = WIT.MOD_NAME,
    version = WIT.VERSION_NUMBER,
    clientSideOnly = true,
    guiFactory = "net.silentchaos512.wit.gui.GuiFactoryWit",
    dependencies = WIT.DEPENDENCIES,
    updateJSON = "https://raw.githubusercontent.com/SilentChaos512/WIT/master/update.json")
//@formatter:on
public class WIT {

  public static final String MOD_ID = "wit";
  public static final String MOD_NAME = "WIT";
  public static final String VERSION_NUMBER = "@VERSION@";
  public static final String DEPENDENCIES = "required-after:forge@[13.19.0.2156,);";

  public static Logger logger = LogManager.getLogger(MOD_NAME);

  public Map<String, ModContainer> mods;
  float lastPartialTicks = 0f;

  public boolean foundStorageDrawers = false;
  public boolean foundMcMultiPart = false;

  @Instance(MOD_ID)
  public static WIT instance;

  @SidedProxy(clientSide = "net.silentchaos512.wit.proxy.ClientProxy")
  public static CommonProxy proxy;

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {

    proxy.preInit();
    Config.init(event.getSuggestedConfigurationFile());
    MinecraftForge.EVENT_BUS.register(this);

    mods = new HashMap<String, ModContainer>();
    for (String key : Loader.instance().getIndexedModList().keySet()) {
      mods.put(key.toLowerCase(), Loader.instance().getIndexedModList().get(key));
    }

    populateBlockReplacements();
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {

    proxy.init();
    LocalizationHelper.init();
    Config.save();
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {

    proxy.postInit();
    foundStorageDrawers = Loader.isModLoaded("StorageDrawers");
    foundMcMultiPart = Loader.isModLoaded("mcmultipart");
  }

  @SubscribeEvent
  public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {

    if (event.getModID().equals(MOD_ID)) {
      Config.load();
      Config.save();
    }
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onTooltip(ItemTooltipEvent event) {

    ItemStackInfo itemInfo = new ItemStackInfo(event.getItemStack());
    ItemStack stack = itemInfo.getStack();
    Item item = itemInfo.getItem();

    // ID & meta
    if (Config.tooltipDisplayIdMeta && event.getFlags() != TooltipFlags.ADVANCED) {
      String str = event.getToolTip().get(0);
      str += " " + Item.getIdFromItem(item) + ":" + stack.getItemDamage();
      event.getToolTip().set(0, str);
    }

    // Tool stats
    if (shouldDipslayToolStats() && item instanceof ItemTool) {
      ItemTool itemTool = (ItemTool) item;
      IBlockState state = getBlockForToolSpeed(itemTool);
      if (state != null) {
        String str = String.format("%.1f", itemTool.getStrVsBlock(stack, state));
        str = LocalizationHelper.instance.get("ToolSpeed", str);
        event.getToolTip().add(str);
      }
      int maxDamage = itemTool.getMaxDamage(stack);
      String str = LocalizationHelper.instance.get("ToolMaxDamage", maxDamage);
      event.getToolTip().add(str);
    }

    // Food stats
    if (shouldDisplayFoodStats() && item instanceof ItemFood) {
      ItemFood itemFood = (ItemFood) item;
      String str = "%d (%.1f)";
      str = String.format(str, itemFood.getHealAmount(stack),
          itemFood.getSaturationModifier(stack));
      str = LocalizationHelper.instance.get("Food", str);
      event.getToolTip().add(str);
    }

    // Ore dictionary entries
    if (shouldDisplayOreDict()) {
      List<String> oreNames = itemInfo.getOreNames();
      if (!oreNames.isEmpty()) {
        event.getToolTip().add("Ore Dictionary:");
        for (String ore : oreNames) {
          event.getToolTip().add("- " + ore);
        }
      }
    }

    // Mod name
    if (shouldDisplayModName()) {
      event.getToolTip()
          .add(Config.formatModName.replaceAll("&", "\u00a7") + itemInfo.getModName());
    }
  }

  public HudRenderObject getRenderObject() {

    Minecraft mc = Minecraft.getMinecraft();
    Entity renderViewEntity = mc.getRenderViewEntity();
    World world = renderViewEntity.world;

    HudRenderObject renderObject = null;

    RayTraceResult mop = mc.objectMouseOver;
    if (mop != null) {
      if (mop.entityHit != null) {
        // Looking at an Entity.
        EntityInfo entityInfo = new EntityInfo(mop.entityHit);
        renderObject = new HudRenderObject(entityInfo);
      } else {
        // Looking at a Block.
        BlockPos pos = mop.getBlockPos();
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() != null && state.getBlock() != Blocks.AIR) {
          // Sunflower bug fix
          IBlockState stateDown = world.getBlockState(pos.down());
          if (Config.enableSunflowerBugfix && state.getBlock() == Blocks.DOUBLE_PLANT
              && stateDown.getBlock() == Blocks.DOUBLE_PLANT) {
            state = stateDown;
          }

          ItemStack blockStack = new ItemStack(state.getBlock());
          // In some cases, blocks have no item (example, lit redstone).
          if (blockStack.getItem() != null) {
            BlockStackInfo blockInfo = new BlockStackInfo(state, pos);
            if (blockInfo.getBlock() != Blocks.AIR && blockInfo.getItem() != null) {
              renderObject = new HudRenderObject(blockInfo);
            }
          } else {
            // Might be able to get item dropped by block?
            // Was getItemDropped, this seems to fix the issue with doors and slabs, but not tall plants.
            ItemStack itemDrop = state.getBlock().getPickBlock(state, mop, world, pos,
                mc.player);
            if (itemDrop != null) {
              ItemStackInfo itemInfo = new ItemStackInfo(itemDrop);
              renderObject = new HudRenderObject(itemInfo);
            }
          }

          // Multipart?
          if (renderObject == null && foundMcMultiPart) {
            renderObject = MCMultiPartHandler.getForMultiPart();
          }
        }
      }
    }

    return renderObject;
  }

  @SubscribeEvent
  public void onRenderOverlay(RenderGameOverlayEvent event) {

    if (event.isCancelable() || event.getType() != RenderGameOverlayEvent.ElementType.TEXT) {
      return;
    }

    GuiScreen screen = Minecraft.getMinecraft().currentScreen;
    if (screen != null && !screen.doesGuiPauseGame() && Config.hudHideWhenGuiOpen) {
      return;
    }

    HudRenderObject renderObject = getRenderObject();

    if (renderObject != null) {
      renderObject.render(event);
    } else {
      HudRenderObject.adjustBackgroundHeight(event, HudRenderObject.lastMaxBackgroundHeight, false);
      HudRenderObject.renderBackground(HudRenderObject.lastMaxBackgroundWidth,
          HudRenderObject.lastBackgroundPosX, HudRenderObject.lastBackgroundPosY);
    }
  }

  @SubscribeEvent
  public void onRenderWorldLastEvent(RenderWorldLastEvent event) {

    // HudRenderObject renderObject = getRenderObject();
    // TODO
  }

  public void populateBlockReplacements() {

    WitBlockReplacements.init();
    WitBlockReplacements rep = WitBlockReplacements.instance;
    if (Config.disguiseMonsterEggBlocks) {
      rep.add(new ItemStack(Blocks.MONSTER_EGG, 1, 0), new ItemStack(Blocks.STONE));
      rep.add(new ItemStack(Blocks.MONSTER_EGG, 1, 1), new ItemStack(Blocks.COBBLESTONE));
      rep.add(new ItemStack(Blocks.MONSTER_EGG, 1, 2), new ItemStack(Blocks.STONEBRICK, 1, 0));
      rep.add(new ItemStack(Blocks.MONSTER_EGG, 1, 3), new ItemStack(Blocks.STONEBRICK, 1, 1));
      rep.add(new ItemStack(Blocks.MONSTER_EGG, 1, 4), new ItemStack(Blocks.STONEBRICK, 1, 2));
      rep.add(new ItemStack(Blocks.MONSTER_EGG, 1, 5), new ItemStack(Blocks.STONEBRICK, 1, 3));
    }
  }

  public boolean shouldDisplayModName() {

    return shouldDisplayObject(Config.tooltipDisplayModName, Config.tooltipDisplayModNameShift);
  }

  public boolean shouldDisplayOreDict() {

    return shouldDisplayObject(Config.tooltipDisplayOreDict, Config.tooltipDisplayOreDictShift);
  }

  public boolean shouldDisplayFoodStats() {

    return shouldDisplayObject(Config.tooltipDisplayFoodStats, Config.tooltipDisplayFoodStatsShift);
  }

  public boolean shouldDipslayToolStats() {

    return shouldDisplayObject(Config.tooltipDisplayToolStats, Config.tooltipDisplayToolStatsShift);
  }

  private boolean shouldDisplayObject(boolean display, boolean shiftOnly) {

    return display ? (shiftOnly && KeyTracker.instance.isShiftPressed()) || !shiftOnly : false;
  }

  private IBlockState getBlockForToolSpeed(ItemTool itemTool) {

    if (itemTool instanceof ItemPickaxe) {
      return Blocks.STONE.getDefaultState();
    } else if (itemTool instanceof ItemSpade) {
      return Blocks.DIRT.getDefaultState();
    } else if (itemTool instanceof ItemAxe) {
      return Blocks.LOG.getDefaultState();
    }
    // Weird generic tool. Naively assume it can mine stone.
    return Blocks.STONE.getDefaultState();
  }
}
