package net.silentchaos512.wit.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;
import net.silentchaos512.utils.Anchor;
import net.silentchaos512.wit.Wit;
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
    private static final Identifier BACKGROUND_TEXTURE = new Identifier(Wit.MOD_ID, "textures/background.png");

    public static boolean renderHud = true;
    private static double backgroundHeight = 0.0;
    // TODO: Why are these public?
    public static int lastMaxBackgroundWidth = 0;
    public static int lastMaxBackgroundHeight = 0;
    public static int lastBackgroundPosX = 0;
    public static int lastBackgroundPosY = 0;
    private static float lastPartialTicks = 0f;

    private final IInfoObject info;
    private final List<TextComponent> lines = new ArrayList<>();
    private final MinecraftClient mc;

    public HudRenderObject(IInfoObject info) {
        this.info = info;
        this.mc = MinecraftClient.getInstance();
    }

    public void render(float partialTicks) {
        if (!renderHud) return;

        // Get text
        if (info != null) {
            info.addLines(mc.player, lines);
        }

        int x = Config.HUD.position.get().getX(mc.window.getScaledWidth(), getWidth(), MARGIN)
                + Config.HUD.offsetX.get();
        int y = Config.HUD.position.get().getY(mc.window.getScaledHeight(), getHeight(), MARGIN)
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
        for (TextComponent text : lines) {
            String line = format(text);
            int lineWidth = mc.fontRenderer.getStringWidth(line);
            int diff = longestWidth - lineWidth;
            mc.fontRenderer.drawWithShadow(line, x + align.getPadding(diff), y, 0xFFFFFF);
            y += VERTICAL_LINE_SPACING + mc.fontRenderer.fontHeight;
        }
    }

    private static String format(TextComponent text) {
        String line = text.getFormattedText();
/*        if (text instanceof TranslatableTextComponent) {
            // For some reason, translated text completely ignores formatting D:
            // This just applies the missing formatting code
            // NOTE: I verified this does not happen in vanilla 1.13.2 for advancement titles
            return text.getStyle().getFormattingCode() + line;
        }*/
        return line;
    }

    public static void adjustBackgroundHeight(float partialTicks, int maxHeight, boolean expand) {
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

        MinecraftClient.getInstance().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        GL11.glColor4d(1, 1, 1, Config.HUD.backgroundOpacity.get());

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vbuffer = tessellator.getBufferBuilder();
        vbuffer.begin(7, VertexFormats.POSITION_UV);
//    vbuffer.putColorRGBA(255, 255, 255, (int) (255 * Config.hudBackgroundOpacity));
        vbuffer.vertex(x, y + height, 0).texture(0, 1).next();
        vbuffer.vertex(x + width, y + height, 0).texture(1, 1).next();
        vbuffer.vertex(x + width, y, 0).texture(1, 0).next();
        vbuffer.vertex(x, y, 0).texture(0, 0).next();
        tessellator.draw();

        GlStateManager.popMatrix();
    }

    private int getWidth() {
        int longest = 0;

        for (TextComponent text : lines) {
            int length = mc.fontRenderer.getStringWidth(format(text));
            longest = length > longest ? length : longest;
        }

        return longest;
    }

    private int getHeight() {
        return mc.fontRenderer.fontHeight * lines.size() + VERTICAL_LINE_SPACING * (lines.size() - 1);
    }
}
