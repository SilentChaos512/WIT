package net.silentchaos512.wit.client;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.silentchaos512.wit.WIT;
import net.silentchaos512.wit.config.Config;
import net.silentchaos512.wit.info.BlockStackInfo;
import net.silentchaos512.wit.info.EntityInfo;
import net.silentchaos512.wit.info.ItemStackInfo;

public class HudRenderObject {

  public static final int VERTICAL_LINE_SPACING = 2;
  public static final int BACKGROUND_PADDING = 3;
  public static final int BACKGROUND_TRANSITION_TIME = 4;

  public static double backgroundHeight = 0.0;
  public static int lastMaxBackgroundWidth = 0;
  public static int lastMaxBackgroundHeight = 0;
  public static int lastBackgroundPosX = 0;
  public static int lastBackgroundPosY = 0;
  public static float lastPartialTicks = 0f;

  BlockStackInfo blockInfo = null;
  ItemStackInfo itemInfo = null;
  EntityInfo entityInfo = null;

  List<String> lines = Lists.newArrayList();

  Minecraft mc = Minecraft.getMinecraft();
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

    if (backgroundHeight <= 0.0) {
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
    GL11.glColor4f(1f, 1f, 1f, 0.8f); // TODO: Configs?

    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    worldrenderer.startDrawingQuads();
    worldrenderer.addVertexWithUV(x, y + height, 0, 0, 1);
    worldrenderer.addVertexWithUV(x + width, y + height, 0, 1, 1);
    worldrenderer.addVertexWithUV(x + width, y, 0, 1, 0);
    worldrenderer.addVertexWithUV(x, y, 0, 0, 0);
    tessellator.draw();
  }

  public void getLinesForBlock(BlockStackInfo info) {

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
    }
  }

  public void getLinesForEntity(EntityInfo info) {

    Entity entity = info.entity;
    String line;

    // Entity name
    line = (entity.hasCustomName() ? EnumChatFormatting.ITALIC : "") + entity.getName();
    line += shouldDisplayIdMeta() ? " [" + EntityList.getEntityID(entity) + "]" : "";
    lines.add(line);

    // Full name
    if (shouldDisplayResourceName()) {
      lines.add(format(Config.formatResourceName) + info.unlocalizedName);
    }

    // Mod name
    if (shouldDisplayModName()) {
      lines.add(format(Config.formatModName) + info.modName);
    }
  }

  public String format(String str) {

    return str.replaceAll("&", "\u00a7");
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

  public boolean shouldDisplayResourceName() {

    if (Config.hudDisplayResourceName) {
      if ((Config.hudDisplayResourceNameShift && sneaking) || !Config.hudDisplayResourceNameShift) {
        return true;
      }
    }
    return false;
  }

  public boolean shouldDisplayModName() {

    if (Config.hudDisplayModName) {
      if ((Config.hudDisplayModNameShift && sneaking) || !Config.hudDisplayModNameShift) {
        return true;
      }
    }
    return false;
  }

  public boolean shouldDisplayIdMeta() {

    if (Config.hudDisplayIdMeta) {
      if ((Config.hudDisplayIdMetaShift && sneaking) || !Config.hudDisplayIdMetaShift) {
        return true;
      }
    }
    return false;
  }
}
