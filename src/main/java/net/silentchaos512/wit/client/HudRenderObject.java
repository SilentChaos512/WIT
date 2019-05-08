package net.silentchaos512.wit.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.utils.Anchor;
import net.silentchaos512.wit.WIT;
import net.silentchaos512.wit.api.IInfoObject;
import net.silentchaos512.wit.config.Config;
import net.silentchaos512.wit.lib.TextAlignment;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class HudRenderObject {
    private static final int VERTICAL_LINE_SPACING = 2;
    private static final int BACKGROUND_PADDING = 3;
    private static final int MARGIN = BACKGROUND_PADDING + 2;
    private static final int BACKGROUND_TRANSITION_TIME = 4;
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(WIT.MOD_ID, "textures/background.png");

    public static boolean renderHud = true;
    private static double backgroundHeight = 0.0;
    private static int lastMaxBackgroundWidth = 0;
    private static int lastMaxBackgroundHeight = 0;
    private static int lastBackgroundPosX = 0;
    private static int lastBackgroundPosY = 0;
    private static float lastPartialTicks = 0f;

    private final IInfoObject info;
    private final List<ITextComponent> lines = new ArrayList<>();
    private final Minecraft mc;

    public HudRenderObject(IInfoObject info) {
        this.info = info;
        this.mc = Minecraft.getInstance();
    }

    public void render(float partialTicks) {
        if (!renderHud) return;

        // Get text
        if (info != null) {
            info.addLines(mc.player, lines);
        }

        int x = Config.HUD.position.get().getX(mc.mainWindow.getScaledWidth(), getWidth(), MARGIN)
                + Config.HUD.offsetX.get();
        int y = Config.HUD.position.get().getY(mc.mainWindow.getScaledHeight(), getHeight(), MARGIN)
                + Config.HUD.offsetY.get();

        // Move bottom center up so it's not over the hotbar
        if (Config.HUD.position.get() == Anchor.BOTTOM_CENTER) {
            y -= 50;
        }

        int longestWidth = getWidth();

        // Render background
        adjustBackgroundHeight(partialTicks, getHeight() + 2 * BACKGROUND_PADDING, true);
        renderBackground(longestWidth, x, y);

        // Render text
        final TextAlignment align = Config.HUD.textAlignment.get();
        for (ITextComponent text : lines) {
            String line = format(text);
            int lineWidth = mc.fontRenderer.getStringWidth(line);
            int diff = longestWidth - lineWidth;
            mc.fontRenderer.drawStringWithShadow(line, x + align.getPadding(diff), y, 0xFFFFFF);
            y += VERTICAL_LINE_SPACING + mc.fontRenderer.FONT_HEIGHT;
        }
    }

    private static String format(ITextComponent text) {
        // In 1.13, formatting was bugged, correction code has been removed,
        // but I'll leave this method for now
        return text.getFormattedText();
    }

    public static void handleClosingAnimation(float partialTicks) {
        adjustBackgroundHeight(partialTicks, lastMaxBackgroundHeight, false);
        renderClosingBackground();
    }

    private static void adjustBackgroundHeight(float partialTicks, int maxHeight, boolean expand) {
        float time = partialTicks - lastPartialTicks;
        if (time < 0f) {
            time += 1f;
        }
        lastPartialTicks = partialTicks;

        lastMaxBackgroundHeight = maxHeight;
        if (backgroundHeight > maxHeight || !expand) {
            backgroundHeight -= time * maxHeight / BACKGROUND_TRANSITION_TIME;
            if (backgroundHeight < 0.0) {
                backgroundHeight = 0.0;
            }
        } else {
            backgroundHeight += time * maxHeight / BACKGROUND_TRANSITION_TIME;
            if (backgroundHeight > maxHeight) {
                backgroundHeight = maxHeight;
            }
        }
    }

    private static void renderClosingBackground() {
        renderBackground(lastMaxBackgroundWidth, lastBackgroundPosX, lastBackgroundPosY);
    }

    private static void renderBackground(int maxWidth, int posX, int posY) {
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

        Minecraft.getInstance().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        GL11.glColor4d(1, 1, 1, Config.HUD.backgroundOpacity.get());

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

    private int getWidth() {
        int longest = 0;

        for (ITextComponent text : lines) {
            int length = mc.fontRenderer.getStringWidth(format(text));
            longest = length > longest ? length : longest;
        }

        return longest;
    }

    private int getHeight() {
        return mc.fontRenderer.FONT_HEIGHT * lines.size() + VERTICAL_LINE_SPACING * (lines.size() - 1);
    }
}
