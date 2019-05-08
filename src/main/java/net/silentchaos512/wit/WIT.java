package net.silentchaos512.wit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.silentchaos512.wit.client.HudRenderObject;
import net.silentchaos512.wit.client.RayTraceHelper;
import net.silentchaos512.wit.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(WIT.MOD_ID)
public class WIT {
    public static final String MOD_ID = "wit";
    public static final String MOD_NAME = "WIT";
    public static final String VERSION_NUMBER = "1.1.2";

    public static Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public static WIT INSTANCE;
    private static SideProxy PROXY;

    public WIT() {
        INSTANCE = this;
        PROXY = DistExecutor.runForDist(() -> () -> new SideProxy.Client(), () -> () -> new SideProxy.Server());

        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, WIT::onRenderOverlay);
    }

    private static void onRenderOverlay(RenderGameOverlayEvent event) {
        if (event.isCancelable() || event.getType() != RenderGameOverlayEvent.ElementType.TEXT) {
            return;
        }

        GuiScreen screen = Minecraft.getInstance().currentScreen;
        if (screen != null && !screen.doesGuiPauseGame() && Config.HUD.hideWhenGuiOpen.get()) {
            return;
        }

        float partialTicks = event.getPartialTicks();
        HudRenderObject renderObject = RayTraceHelper.getRenderObject(partialTicks);

        if (renderObject != null) {
            renderObject.render(partialTicks);
        } else {
            HudRenderObject.handleClosingAnimation(partialTicks);
        }
    }
}
