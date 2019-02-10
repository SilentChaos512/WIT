package net.silentchaos512.wit.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.hud.InGameHud;
import net.silentchaos512.wit.client.HudRenderObject;
import net.silentchaos512.wit.client.RayTraceHelper;
import net.silentchaos512.wit.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud extends Drawable {
    @Inject(at = @At("RETURN"), method = "draw")
    private void onDraw(float float_1, CallbackInfo info) {
        Screen screen = MinecraftClient.getInstance().currentScreen;
        if (screen != null && !screen.isPauseScreen() && Config.HUD.hideWhenGuiOpen.get()) {
            return;
        }

        HudRenderObject renderObject = RayTraceHelper.getRenderObject(float_1);

        if (renderObject != null) {
            renderObject.render(float_1);
        } else {
            HudRenderObject.adjustBackgroundHeight(float_1, HudRenderObject.lastMaxBackgroundHeight, false);
            HudRenderObject.renderBackground(HudRenderObject.lastMaxBackgroundWidth,
                    HudRenderObject.lastBackgroundPosX, HudRenderObject.lastBackgroundPosY);
        }
    }
}
