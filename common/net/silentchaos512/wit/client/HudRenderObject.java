package net.silentchaos512.wit.client;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.stream.ChatController.EnumChannelState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeVersion;
import net.silentchaos512.wit.WIT;
import net.silentchaos512.wit.api.IWitHudInfo;
import net.silentchaos512.wit.config.Config;
import net.silentchaos512.wit.info.BlockStackInfo;
import net.silentchaos512.wit.info.EntityInfo;
import net.silentchaos512.wit.info.ItemStackInfo;
import net.silentchaos512.wit.lib.LocalizationHelper;

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

    float time = event.partialTicks - lastPartialTicks;
    if (time < 0f) {
      time += 1f;
    }
    lastPartialTicks = event.partialTicks;

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
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
    worldrenderer.pos(x, y + height, 0).tex(0, 1).endVertex();
    worldrenderer.pos(x + width, y + height, 0).tex(1, 1).endVertex();
    worldrenderer.pos(x + width, y, 0).tex(1, 0).endVertex();
    worldrenderer.pos(x, y, 0).tex(0, 0).endVertex();
    tessellator.draw();
  }

  public void getLinesForBlock(BlockStackInfo info) {

    // Name, ID, meta, tile entity
    String line = Config.hudObjectName.shouldDisplay(player)
        ? info.item.getRarity(info.stack).rarityColor + info.localizedName : "";
    line += Config.hudIdMeta.shouldDisplay(player)
        ? Config.hudIdMeta.formatString(" [" + info.blockId + ":" + info.meta + "]") : "";
    if (info.tileEntity != null && Config.hudTileEntity.shouldDisplay(player)) {
      line += Config.hudTileEntity.formatString(" (TE)");
    }
    lines.add(line);

    // Harvestability
    if (Config.hudHarvestable.shouldDisplay(player)) {
      boolean canHarvest = info.meta >= 0 && info.meta < 16 // Bad metadata check
          && ForgeHooks.canHarvestBlock(info.block, player, player.worldObj, info.pos)
          && info.block.getBlockHardness(player.worldObj, info.pos) >= 0;
      String format = canHarvest ? Config.hudHarvestable.formatString("")
          : Config.hudHarvestable.formatString2("");

      if (info.harvestTool != null && info.harvestLevel > -1) {
        line = loc.get("HarvestWith");
        String tool = loc.get("Tool." + info.harvestTool);
        line = String.format(line, tool, info.harvestLevel);
      } else {
        line = loc.get((canHarvest ? "" : "Not") + "Harvestable");
      }
      lines.add(format + line);
    }

    // Full (resource) name
    if (Config.hudResourceName.shouldDisplay(player)) {
      lines.add(Config.hudResourceName
          .formatString(info.modId + ":" + info.resourceLocation.getResourcePath()));
    }

    // Block specific info?
    if (info.block instanceof IWitHudInfo) {
      List<String> extraList = ((IWitHudInfo) info.block).getWitLines(info.state, info.pos, player,
          Config.hudAdvancedMode);
      if (extraList != null) {
        lines.addAll(extraList);
      }
    }

    // Mod name
    if (Config.hudModName.shouldDisplay(player)) {
      lines.add(Config.hudModName.formatString(info.modName));
    }
  }

  public void getLinesForItem(ItemStackInfo info) {

    // Name, ID, meta, tile entity
    String line = Config.hudObjectName.shouldDisplay(player)
        ? info.item.getRarity(info.stack).rarityColor + info.localizedName : "";
    line += Config.hudIdMeta.shouldDisplay(player) ? Config.hudIdMeta.formatString(
        " [" + Item.getIdFromItem(info.item) + ":" + info.stack.getItemDamage() + "]") : "";
    lines.add(line);

    // Full (resource) name
    if (Config.hudResourceName.shouldDisplay(player)) {
      lines.add(Config.hudResourceName
          .formatString(info.modId + ":" + info.resourceLocation.getResourcePath()));
    }

    // Mod name
    if (Config.hudModName.shouldDisplay(player)) {
      lines.add(Config.hudModName.formatString(info.modName));
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

    // Full name
    if (Config.hudResourceName.shouldDisplay(player)) {
      lines.add(Config.hudResourceName.formatString(info.unlocalizedName));
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
