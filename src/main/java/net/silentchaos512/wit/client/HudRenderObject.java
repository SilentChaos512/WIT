package net.silentchaos512.wit.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
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
    private static final int BACKGROUND_TRANSITION_TIME = 4;
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(WIT.MOD_ID, "textures/background.png");

    private static boolean renderHud = true;
    private static double backgroundHeight = 0.0;
    // TODO: Why are these public?
    public static int lastMaxBackgroundWidth = 0;
    public static int lastMaxBackgroundHeight = 0;
    public static int lastBackgroundPosX = 0;
    public static int lastBackgroundPosY = 0;
    private static float lastPartialTicks = 0f;

    private final IInfoObject info;

    private final List<ITextComponent> lines = new ArrayList<>();

    public HudRenderObject(IInfoObject info) {
        this.info = info;
    }

    public void render(RenderGameOverlayEvent event) {
        if (!renderHud) return;

        Minecraft mc = Minecraft.getInstance();

        // Get text
        if (info != null) {
            info.addLines(mc.player, lines);
        }

        // FIXME
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
        String line = text.getFormattedText();
        if (text instanceof TextComponentTranslation) {
            // For some reason, translated text completely ignores formatting D:
            // This just applies the missing formatting code
            return text.getStyle().getFormattingCode() + line;
        }
        return line;
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
        Minecraft mc = Minecraft.getInstance();
        int longest = 0;

        for (ITextComponent text : lines) {
            int length = mc.fontRenderer.getStringWidth(format(text));
            longest = length > longest ? length : longest;
        }

        return longest;
    }

    private int getHeight() {
        Minecraft mc = Minecraft.getInstance();
        return mc.fontRenderer.FONT_HEIGHT * lines.size() + VERTICAL_LINE_SPACING * (lines.size() - 1);
    }
}
