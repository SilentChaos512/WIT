package net.silentchaos512.wit.client;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.silentchaos512.wit.WIT;
import net.silentchaos512.wit.api.IInfoObject;
import net.silentchaos512.wit.config.Config;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class HudRenderObject {
    public static final int VERTICAL_LINE_SPACING = 2;
    public static final int BACKGROUND_PADDING = 3;
    public static final int BACKGROUND_TRANSITION_TIME = 4;
    public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(WIT.MOD_ID, "textures/background.png");

    public static boolean renderHud = true;
    public static double backgroundHeight = 0.0;
    public static int lastMaxBackgroundWidth = 0;
    public static int lastMaxBackgroundHeight = 0;
    public static int lastBackgroundPosX = 0;
    public static int lastBackgroundPosY = 0;
    public static float lastPartialTicks = 0f;

    private final IInfoObject info;

    private final List<ITextComponent> lines = Lists.newArrayList();

    public HudRenderObject(IInfoObject info) {
        this.info = info;
    }

    public void render(RenderGameOverlayEvent event) {
        if (!renderHud) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();

        // Get text
        if (info != null) {
            info.addLines(mc.player, lines);
        }

//        Tuple position = Config.hudPosition.getStartingPosition(this, resolution);
        int x = 5;
        int y = 5;
//        x += Config.hudOffsetX * resolution.getScaledWidth();
//        y += Config.hudOffsetY * resolution.getScaledHeight();

        int longestWidth = getWidth();

        // Render background
        adjustBackgroundHeight(event, getHeight() + 2 * BACKGROUND_PADDING, true);
        renderBackground(longestWidth, x, y);

        // Render text
        for (ITextComponent text : lines) {
            String line = text.getFormattedText();
            int lineWidth = mc.fontRenderer.getStringWidth(line);
            int diff = longestWidth - lineWidth;
            mc.fontRenderer.drawStringWithShadow(line, x + Config.hudJustification.getPadding(diff), y, 0xFFFFFF);
            y += VERTICAL_LINE_SPACING + mc.fontRenderer.FONT_HEIGHT;
        }
    }

    public static void adjustBackgroundHeight(RenderGameOverlayEvent event, int maxHeight, boolean expand) {
        float time = event.getPartialTicks() - lastPartialTicks;
        if (time < 0f) {
            time += 1f;
        }
        lastPartialTicks = event.getPartialTicks();

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

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();

        Minecraft.getInstance().textureManager.bindTexture(BACKGROUND_TEXTURE);
        GL11.glColor4f(1f, 1f, 1f, Config.hudBackgroundOpacity);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vbuffer = tessellator.getBuffer();
        vbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
//    vbuffer.putColorRGBA(255, 255, 255, (int) (255 * Config.hudBackgroundOpacity));
        vbuffer.pos(x, y + height, 0).tex(0, 1).endVertex();
        vbuffer.pos(x + width, y + height, 0).tex(1, 1).endVertex();
        vbuffer.pos(x + width, y, 0).tex(1, 0).endVertex();
        vbuffer.pos(x, y, 0).tex(0, 0).endVertex();
        tessellator.draw();

        GlStateManager.popMatrix();
    }

    public int getWidth() {
        Minecraft mc = Minecraft.getInstance();
        int longest = 0;

        for (ITextComponent text : lines) {
            int length = mc.fontRenderer.getStringWidth(text.getFormattedText());
            longest = length > longest ? length : longest;
        }

        return longest;
    }

    public int getHeight() {
        Minecraft mc = Minecraft.getInstance();
        return mc.fontRenderer.FONT_HEIGHT * lines.size() + VERTICAL_LINE_SPACING * (lines.size() - 1);
    }
}
