package net.silentchaos512.wit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
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
import net.silentchaos512.wit.client.HudRenderObject;
import net.silentchaos512.wit.client.key.KeyTracker;
import net.silentchaos512.wit.config.Config;
import net.silentchaos512.wit.info.BlockStackInfo;
import net.silentchaos512.wit.info.EntityInfo;
import net.silentchaos512.wit.info.ItemStackInfo;
import net.silentchaos512.wit.proxy.CommonProxy;

@Mod(modid = WIT.MOD_ID, name = WIT.MOD_NAME, version = WIT.VERSION_NUMBER)
public class WIT {

  public static final String MOD_ID = "WIT";
  public static final String MOD_NAME = "WIT";
  public static final String VERSION_NUMBER = "@VERSION@";

  public static Logger logger = LogManager.getLogger(MOD_NAME);

  public Map<String, ModContainer> mods;
  float lastPartialTicks = 0f;

  @Instance(MOD_ID)
  public static WIT instance;

  @SidedProxy(clientSide = "net.silentchaos512.wit.proxy.ClientProxy", serverSide = "net.silentchaos512.wit.proxy.CommonProxy")
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
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {

    proxy.init();
    Config.save();
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent event) {

    proxy.postInit();
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

  public boolean shouldDisplayModName() {

    if (Config.tooltipDisplayModName) {
      if ((Config.tooltipDisplayModNameShift && KeyTracker.instance.isShiftPressed())
          || !Config.hudDisplayModNameShift) {
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
