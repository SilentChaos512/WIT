package net.silentchaos512.wit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.silentchaos512.wit.api.WitBlockReplacements;
import net.silentchaos512.wit.client.HudRenderObject;
import net.silentchaos512.wit.client.key.KeyTracker;
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
    updateJSON = "https://raw.githubusercontent.com/SilentChaos512/WIT/master/update.json")
//@formatter:on
public class WIT {

  public static final String MOD_ID = "WIT";
  public static final String MOD_NAME = "WIT";
  public static final String VERSION_NUMBER = "@VERSION@";

  public static Logger logger = LogManager.getLogger(MOD_NAME);

  public Map<String, ModContainer> mods;
  float lastPartialTicks = 0f;

  public boolean foundStorageDrawers = false;

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
  }

  @SubscribeEvent
  public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {

    if (event.modID.equals(MOD_ID)) {
      Config.load();
      Config.save();
    }
  }

  @SubscribeEvent
  public void onTooltip(ItemTooltipEvent event) {

    ItemStackInfo itemInfo = new ItemStackInfo(event.itemStack);

    // Ore dictionary entries
    if (shouldDisplayOreDict()) {
      List<String> oreNames = itemInfo.getOreNames();
      if (!oreNames.isEmpty()) {
        event.toolTip.add("Ore Dictionary:");
        for (String ore : oreNames) {
          event.toolTip.add("- " + ore);
        }
      }
    }

    if (shouldDisplayModName()) {
      event.toolTip.add(Config.formatModName.replaceAll("&", "\u00a7") + itemInfo.modName);
    }
  }

  @SubscribeEvent
  public void onRenderOverlay(RenderGameOverlayEvent event) {

    if (event.isCancelable() || event.type != RenderGameOverlayEvent.ElementType.TEXT) {
      return;
    }

    Minecraft mc = Minecraft.getMinecraft();
    Entity renderViewEntity = mc.getRenderViewEntity();
    World world = renderViewEntity.worldObj;

    HudRenderObject renderObject = null;

    MovingObjectPosition mop = mc.objectMouseOver;
    if (mop != null) {
      if (mop.entityHit != null) {
        // Looking at an Entity.
        EntityInfo entityInfo = new EntityInfo(mop.entityHit);
        renderObject = new HudRenderObject(entityInfo);
      } else {
        // Looking at a Block.
        BlockPos pos = mop.getBlockPos();
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() != null && state.getBlock() != Blocks.air) {
          ItemStack blockStack = new ItemStack(state.getBlock());
          // In some cases, blocks have no item (example, lit redstone).
          if (blockStack.getItem() != null) {
            BlockStackInfo blockInfo = new BlockStackInfo(state, pos);
            if (blockInfo.block != Blocks.air && blockInfo.item != null) {
              renderObject = new HudRenderObject(blockInfo);
            }
          } else {
            // Might be able to get item dropped by block?
            Item itemDrop = state.getBlock().getItemDropped(state, world.rand, 0);
            if (itemDrop != null) {
              ItemStackInfo itemInfo = new ItemStackInfo(new ItemStack(itemDrop));
              renderObject = new HudRenderObject(itemInfo);
            }
          }
        }
      }
    }

    if (renderObject != null) {
      renderObject.render(event);
    } else {
      HudRenderObject.adjustBackgroundHeight(event, HudRenderObject.lastMaxBackgroundHeight, false);
      HudRenderObject.renderBackground(HudRenderObject.lastMaxBackgroundWidth,
          HudRenderObject.lastBackgroundPosX, HudRenderObject.lastBackgroundPosY);
    }
  }

  public void populateBlockReplacements() {

    WitBlockReplacements.init();
    WitBlockReplacements rep = WitBlockReplacements.instance;
    rep.add(new ItemStack(Blocks.monster_egg, 1, 0), new ItemStack(Blocks.stone));
    rep.add(new ItemStack(Blocks.monster_egg, 1, 1), new ItemStack(Blocks.cobblestone));
    rep.add(new ItemStack(Blocks.monster_egg, 1, 2), new ItemStack(Blocks.stonebrick, 1, 0));
    rep.add(new ItemStack(Blocks.monster_egg, 1, 3), new ItemStack(Blocks.stonebrick, 1, 1));
    rep.add(new ItemStack(Blocks.monster_egg, 1, 4), new ItemStack(Blocks.stonebrick, 1, 2));
    rep.add(new ItemStack(Blocks.monster_egg, 1, 5), new ItemStack(Blocks.stonebrick, 1, 3));
  }

  public boolean shouldDisplayModName() {

    if (Config.tooltipDisplayModName) {
      if ((Config.tooltipDisplayModNameShift && KeyTracker.instance.isShiftPressed())
          || !Config.tooltipDisplayModNameShift) {
        return true;
      }
    }
    return false;
  }

  public boolean shouldDisplayOreDict() {

    if (Config.tooltipDisplayOreDict) {
      if ((Config.tooltipDisplayOreDictShift && KeyTracker.instance.isShiftPressed())
          || !Config.tooltipDisplayOreDictShift) {
        return true;
      }
    }
    return false;
  }
}
