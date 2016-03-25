package net.silentchaos512.wit.client;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;

import cofh.api.energy.IEnergyHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
<<<<<<< HEAD
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
=======
import net.minecraft.item.Item;
import net.minecraft.util.EnumChatFormatting;
>>>>>>> 636846077d62a83cfce470a263bd2292e65ce35f
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.silentchaos512.wit.WIT;
import net.silentchaos512.wit.api.IWitHudInfo;
import net.silentchaos512.wit.api.WitBlockInfoEvent;
import net.silentchaos512.wit.api.WitEntityInfoEvent;
import net.silentchaos512.wit.config.Config;
import net.silentchaos512.wit.info.BlockStackInfo;
import net.silentchaos512.wit.info.EntityInfo;
import net.silentchaos512.wit.info.ItemStackInfo;
<<<<<<< HEAD
import net.silentchaos512.wit.lib.LocalizationHelper;
=======
>>>>>>> 636846077d62a83cfce470a263bd2292e65ce35f

public class HudRenderObject {

  public static final int VERTICAL_LINE_SPACING = 2;
  public static final int BACKGROUND_PADDING = 3;
  public static final int BACKGROUND_TRANSITION_TIME = 4;

  public static boolean renderHud = true;
  public static double backgroundHeight = 0.0;
  public static int lastMaxBackgroundWidth = 0;
  public static int lastMaxBackgroundHeight = 0;
  public static int lastBackgroundPosX = 0;
  public static int lastBackgroundPosY = 0;
  public static float lastPartialTicks = 0f;

  BlockStackInfo blockInfo = null;
  ItemStackInfo itemInfo = null;
  EntityInfo entityInfo = null;

  LocalizationHelper loc = LocalizationHelper.instance;

  List<String> lines = Lists.newArrayList();

  Minecraft mc = Minecraft.getMinecraft();
  EntityPlayer player = mc.thePlayer;
  FontRenderer fontRender = mc.fontRendererObj;
  boolean sneaking = Minecraft.getMinecraft().thePlayer.isSneaking();

  public HudRenderObject(BlockStackInfo blockInfo) {

    this.blockInfo = blockInfo;
    getLinesForBlock(blockInfo);
  }

  public HudRenderObject(ItemStackInfo itemInfo) {

    this.itemInfo = itemInfo;
    getLinesForItem(itemInfo);
  }

  public HudRenderObject(EntityInfo entityInfo) {

    this.entityInfo = entityInfo;
    getLinesForEntity(entityInfo);
  }

  public void render(RenderGameOverlayEvent event) {

    if (!renderHud) {
      return;
    }

    Tuple position = Config.hudPosition.getStartingPosition(this);
    int x = (Integer) position.getFirst();
    int y = (Integer) position.getSecond();
    // System.out.println(x + ", " + y);
    int lineWidth, diff;
    int longestWidth = getWidth();

    // Render background
    adjustBackgroundHeight(event, getHeight() + 2 * BACKGROUND_PADDING, true);
    renderBackground(longestWidth, x, y);

    // Render text
    for (String line : lines) {
      lineWidth = fontRender.getStringWidth(line);
      diff = longestWidth - lineWidth;
      fontRender.drawStringWithShadow(line, x + Config.hudJustification.getPadding(diff), y,
          0xFFFFFF);
      y += VERTICAL_LINE_SPACING + fontRender.FONT_HEIGHT;
    }
  }

  public static void adjustBackgroundHeight(RenderGameOverlayEvent event, int maxHeight,
      boolean expand) {

<<<<<<< HEAD
    float time = event.getPartialTicks() - lastPartialTicks;
    if (time < 0f) {
      time += 1f;
    }
    lastPartialTicks = event.getPartialTicks();
=======
    float time = event.partialTicks - lastPartialTicks;
    if (time < 0f) {
      time += 1f;
    }
    lastPartialTicks = event.partialTicks;
>>>>>>> 636846077d62a83cfce470a263bd2292e65ce35f

    lastMaxBackgroundHeight = maxHeight;
    if (backgroundHeight > maxHeight || !expand) {
      backgroundHeight -= time * maxHeight / BACKGROUND_TRANSITION_TIME;
      if (backgroundHeight < 0.0) {
        backgroundHeight = 0.0;
      }
    } else if (expand) {
      backgroundHeight += time * maxHeight / BACKGROUND_TRANSITION_TIME;
      if (backgroundHeight > maxHeight) {
        backgroundHeight = maxHeight;
      }
    }
  }

  public static void renderBackground(int maxWidth, int posX, int posY) {

    if (!renderHud || backgroundHeight <= 0.0) {
      return;
    }

    double heightDifference = lastMaxBackgroundHeight - backgroundHeight;
    lastMaxBackgroundWidth = maxWidth;
    lastBackgroundPosX = posX;
    lastBackgroundPosY = posY;

    double x = posX - BACKGROUND_PADDING;
    double y = posY - BACKGROUND_PADDING + heightDifference / 2;
    double width = maxWidth + 2 * BACKGROUND_PADDING;
    double height = backgroundHeight;

    Minecraft.getMinecraft().renderEngine
        .bindTexture(new ResourceLocation(WIT.MOD_ID, "textures/background.png"));
    GL11.glColor4f(1f, 1f, 1f, Config.hudBackgroundOpacity);

    Tessellator tessellator = Tessellator.getInstance();
    VertexBuffer vbuffer = tessellator.getBuffer();
    vbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
    vbuffer.pos(x, y + height, 0).tex(0, 1).endVertex();
    vbuffer.pos(x + width, y + height, 0).tex(1, 1).endVertex();
    vbuffer.pos(x + width, y, 0).tex(1, 0).endVertex();
    vbuffer.pos(x, y, 0).tex(0, 0).endVertex();
    tessellator.draw();
  }

  public void getLinesForBlock(BlockStackInfo info) {
<<<<<<< HEAD

    boolean isIInventory = info.tileEntity instanceof IInventory;
    boolean isIWitHudInfo = info.block instanceof IWitHudInfo;

    // Name, ID, meta, tile entity
    String line = Config.hudObjectName.shouldDisplay(player)
        ? info.item.getRarity(info.stack).rarityColor + info.localizedName : "";
    line += Config.hudIdMeta.shouldDisplay(player)
        ? Config.hudIdMeta.formatString(" [" + info.blockId + ":" + info.meta + "]") : "";
    if (info.tileEntity != null && Config.hudTileEntity.shouldDisplay(player)) {
      line += Config.hudTileEntity.formatString(" (TE)");
    }
    lines.add(line);

    // Inventory?
    getLinesForBlockInventory(info, isIInventory, isIWitHudInfo);
    // Mob spawner?
    getLinesForMobSpawner(info);
    // RF storage?
    getLinesForRfEnergyHandler(info);

    // Harvestability
    getLinesForBlockHarvestability(info);

    // Full (resource) name
    if (Config.hudResourceName.shouldDisplay(player)) {
      lines.add(Config.hudResourceName
          .formatString(info.modId + ":" + info.resourceLocation.getResourcePath()));
    }

    // Block specific info?
    if (isIWitHudInfo) {
      List<String> extraList = ((IWitHudInfo) info.block).getWitLines(info.state, info.pos, player,
          Config.hudAdvancedMode);
      if (extraList != null) {
        lines.addAll(extraList);
      }
    }

    // WIT HUD info event
    WitBlockInfoEvent event = new WitBlockInfoEvent(player, Config.hudAdvancedMode, info.pos,
        info.state);
    if (!MinecraftForge.EVENT_BUS.post(event)) {
      lines.addAll(event.lines);
    }

    // Mod name
    if (Config.hudModName.shouldDisplay(player)) {
      lines.add(Config.hudModName.formatString(info.modName));
    }
  }

  public void getLinesForBlockInventory(BlockStackInfo info, boolean isIInventory,
      boolean isIWitHudInfo) {

    if (!Config.hudBlockInventory.shouldDisplay(player)) {
      return;
    }

    String str = "%s (%d)";

    // Storage Drawers?
    if (WIT.instance.foundStorageDrawers && info.tileEntity instanceof IDrawerGroup) {
      IDrawerGroup drawers = (IDrawerGroup) info.tileEntity;
      for (int i = 0; i < drawers.getDrawerCount(); ++i) {
        IDrawer d = drawers.getDrawer(i);
        if (d != null && d.getStoredItemPrototype() != null) {
          int count = d.getStoredItemCount();
          String name = d.getStoredItemPrototype().getDisplayName();
          lines.add(Config.hudBlockInventory.formatString(String.format(str, name, count)));
        }
      }
    }
    // Generic inventory handler.
    else if (isIInventory && !isIWitHudInfo) {
      List<ItemStack> invStacks = getInventoryStacks((IInventory) info.tileEntity);
      // Display first n items according to config setting.
      for (int i = 0; i < invStacks.size() && i < Config.hudInventoryMaxListCount; ++i) {
        ItemStack stack = invStacks.get(i);
        if (stack != null) {
          int count = stack.stackSize;
          String name = stack.getDisplayName();
          lines.add(Config.hudBlockInventory.formatString(String.format(str, name, count)));
        }
      }
      // How many did we not display?
      int omittedCount = invStacks.size() - Config.hudInventoryMaxListCount;
      if (omittedCount > 0) {
        String str2 = LocalizationHelper.instance.get("OmittedInventoryItems");
        lines.add(String.format(str2, omittedCount));
      }
    }
  }

  public void getLinesForMobSpawner(BlockStackInfo info) {

    if (info.tileEntity instanceof TileEntityMobSpawner) {
      TileEntityMobSpawner tile = (TileEntityMobSpawner) info.tileEntity;
      Entity entity = tile.getSpawnerBaseLogic().getCachedEntity();
      if (entity != null) {
        EntityInfo entityInfo = new EntityInfo(entity);
        lines.add(entityInfo.localizedName);
      }
    }
  }

  public void getLinesForRfEnergyHandler(BlockStackInfo info) {

    if (info.tileEntity instanceof IEnergyHandler && !(info.block instanceof IWitHudInfo)) {
      IEnergyHandler tile = (IEnergyHandler) info.tileEntity;
      int current = tile.getEnergyStored(EnumFacing.UP);
      int max = tile.getMaxEnergyStored(EnumFacing.UP);
      String str = LocalizationHelper.instance.get("RFStorage");
      str = String.format(str, current, max);
      lines.add(str);
    }
  }

  public List<ItemStack> getInventoryStacks(IInventory inv) {

    List<ItemStack> list = Lists.newArrayList();
    ItemStack stack;
    for (int i = 0; i < inv.getSizeInventory(); ++i) {
      stack = inv.getStackInSlot(i);
      if (stack != null) {
        list.add(stack);
      }
=======

    // Name, ID, meta, tile entity
    String line = info.item.getRarity(info.stack).rarityColor + info.localizedName;
    line += shouldDisplayIdMeta() ? " [" + info.blockId + ":" + info.meta + "]" : "";
    if (info.tileEntity != null) {
      line += EnumChatFormatting.GRAY + " (TE)";
    }
    lines.add(line);

    // Full name
    if (shouldDisplayResourceName()) {
      lines.add(format(Config.formatResourceName) + info.modId + ":"
          + info.resourceLocation.getResourcePath());
    }

    // Mod name
    if (shouldDisplayModName()) {
      lines.add(format(Config.formatModName) + info.modName);
    }
  }

  public void getLinesForItem(ItemStackInfo info) {

    // Name, ID, meta, tile entity
    String line = info.item.getRarity(info.stack).rarityColor + info.localizedName;
    line += shouldDisplayIdMeta()
        ? " [" + Item.getIdFromItem(info.item) + ":" + info.stack.getItemDamage() + "]" : "";
    lines.add(line);

    // Full name
    if (shouldDisplayResourceName()) {
      lines.add(format(Config.formatResourceName) + info.modId + ":"
          + info.resourceLocation.getResourcePath());
    }

    // Mod name
    if (shouldDisplayModName()) {
      lines.add(format(Config.formatModName) + info.modName);
>>>>>>> 636846077d62a83cfce470a263bd2292e65ce35f
    }
    return list;
  }

<<<<<<< HEAD
  public void getLinesForBlockHarvestability(BlockStackInfo info) {

    if (!Config.hudHarvestable.shouldDisplay(player)) {
      return;
    }

    Block actualBlock = player.worldObj.getBlockState(info.pos).getBlock();
    boolean canHarvest = info.meta >= 0 && info.meta < 16 // Bad metadata check
        && ForgeHooks.canHarvestBlock(actualBlock, player, player.worldObj, info.pos)
        && info.block.getBlockHardness(info.state, player.worldObj, info.pos) >= 0;
    String format = canHarvest ? Config.hudHarvestable.formatString("")
        : Config.hudHarvestable.formatString2("");

=======
  public void getLinesForEntity(EntityInfo info) {

    Entity entity = info.entity;
>>>>>>> 636846077d62a83cfce470a263bd2292e65ce35f
    String line;
    if (info.harvestTool != null && info.harvestLevel > -1) {
      line = loc.get("HarvestWith");
      String tool = loc.get("Tool." + info.harvestTool);
      line = String.format(line, tool, info.harvestLevel);
    } else {
      line = loc.get((canHarvest ? "" : "Not") + "Harvestable");
    }
    lines.add(format + line);
  }

  public void getLinesForItem(ItemStackInfo info) {

    // Name, ID, meta, tile entity
    String line = Config.hudObjectName.shouldDisplay(player)
        ? info.item.getRarity(info.stack).rarityColor + info.localizedName : "";
    line += Config.hudIdMeta.shouldDisplay(player) ? Config.hudIdMeta.formatString(
        " [" + Item.getIdFromItem(info.item) + ":" + info.stack.getItemDamage() + "]") : "";
    lines.add(line);

<<<<<<< HEAD
    // Full (resource) name
    if (Config.hudResourceName.shouldDisplay(player)) {
      lines.add(Config.hudResourceName
          .formatString(info.modId + ":" + info.resourceLocation.getResourcePath()));
    }

    // Mod name
    if (Config.hudModName.shouldDisplay(player)) {
      lines.add(Config.hudModName.formatString(info.modName));
=======
    // Full name
    if (shouldDisplayResourceName()) {
      lines.add(format(Config.formatResourceName) + info.unlocalizedName);
    }

    // Mod name
    if (shouldDisplayModName()) {
      lines.add(format(Config.formatModName) + info.modName);
>>>>>>> 636846077d62a83cfce470a263bd2292e65ce35f
    }
  }

  public void getLinesForEntity(EntityInfo info) {

    Entity entity = info.entity;
    String line;

    // Entity name
    line = Config.hudObjectName.shouldDisplay(player) ? entity.getDisplayName().getFormattedText()
        : "";
    line += Config.hudIdMeta.shouldDisplay(player)
        ? Config.hudIdMeta.formatString(" [" + EntityList.getEntityID(entity) + "]") : "";
    lines.add(line);

    // Health
    if (info.entity instanceof EntityLiving) {
      EntityLiving entityLiving = (EntityLiving) info.entity;
      line = Config.hudEntityHealth.shouldDisplay(player)
          ? LocalizationHelper.instance.get("EntityHealth") : "";
      line = String.format(line, entityLiving.getHealth(), entityLiving.getMaxHealth());
      lines.add(line);
    }

    // Full name
    if (Config.hudResourceName.shouldDisplay(player)) {
      lines.add(Config.hudResourceName.formatString(info.unlocalizedName));
    }

    // WIT HUD info event
    WitEntityInfoEvent event = new WitEntityInfoEvent(player, Config.hudAdvancedMode, entity);
    if (!MinecraftForge.EVENT_BUS.post(event)) {
      lines.addAll(event.lines);
    }

    // Mod name
    if (Config.hudModName.shouldDisplay(player)) {
      lines.add(Config.hudModName.formatString(info.modName));
    }
  }

  public int getWidth() {

    int longest = 0;
    int length;
    for (String line : lines) {
      length = fontRender.getStringWidth(line);
      longest = length > longest ? length : longest;
    }
    return longest;
  }

  public int getHeight() {

    return fontRender.FONT_HEIGHT * lines.size() + VERTICAL_LINE_SPACING * (lines.size() - 1);
  }
}
