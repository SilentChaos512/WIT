package net.silentchaos512.wit;

import net.fabricmc.api.ModInitializer;
import net.silentchaos512.wit.api.WitBlockReplacements;
import net.silentchaos512.wit.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Wit implements ModInitializer {
    public static final String MOD_ID = "wit";
    public static final String MOD_NAME = "Wit: Fabric Edition";
    public static final String VERSION_NUMBER = "1.0.0";

    public static Logger LOGGER = LogManager.getLogger(MOD_NAME);

    public boolean foundStorageDrawers = false;
    public boolean foundMcMultiPart = false;

    public static Wit INSTANCE;

    public Wit() {
        INSTANCE = this;
//        Config.load();

//        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, Wit::onRenderOverlay);
    }

/*    private static void onRenderOverlay(RenderGameOverlayEvent event) {
        if (event.isCancelable() || event.getType() != RenderGameOverlayEvent.ElementType.TEXT) {
            return;
        }

        GuiScreen screen = Minecraft.getInstance().currentScreen;
        if (screen != null && !screen.doesGuiPauseGame() && Config.HUD.hideWhenGuiOpen.get()) {
            return;
        }

        HudRenderObject renderObject = RayTraceHelper.getRenderObject(event.getPartialTicks());

        if (renderObject != null) {
            renderObject.render(event);
        } else {
            HudRenderObject.adjustBackgroundHeight(event, HudRenderObject.lastMaxBackgroundHeight, false);
            HudRenderObject.renderBackground(HudRenderObject.lastMaxBackgroundWidth,
                    HudRenderObject.lastBackgroundPosX, HudRenderObject.lastBackgroundPosY);
        }
    }*/

    @Override
    public void onInitialize() {
        Config.init();
        WitBlockReplacements.init();
    }
}
