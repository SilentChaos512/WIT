package net.silentchaos512.wit.client.key;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.silentchaos512.wit.WIT;
import net.silentchaos512.wit.client.HudRenderObject;
import org.lwjgl.glfw.GLFW;

public final class KeyTracker {
    private static KeyTracker INSTANCE;

    private final KeyBinding toggleHud;
    private final KeyBinding toggleAdvancedMode;

    private KeyTracker() {
        toggleHud = new KeyBinding("Toggle HUD Overlay", GLFW.GLFW_KEY_KP_1, WIT.MOD_NAME);
        toggleHud.setKeyConflictContext(KeyConflictContext.IN_GAME);
        ClientRegistry.registerKeyBinding(toggleHud);
        toggleAdvancedMode = new KeyBinding("Toggle Advanced Mode", GLFW.GLFW_KEY_KP_4, WIT.MOD_NAME);
        ClientRegistry.registerKeyBinding(toggleAdvancedMode);

        MinecraftForge.EVENT_BUS.addListener(this::onKeyInput);
    }

    public static void init() {
        if (INSTANCE != null) return;
        INSTANCE = new KeyTracker();
    }

    private void onKeyInput(KeyInputEvent event) {
        if (toggleHud.isPressed()) {
            HudRenderObject.renderHud = !HudRenderObject.renderHud;
        } else if (toggleAdvancedMode.isPressed()) {
//            Config.hudAdvancedMode = !Config.hudAdvancedMode;
//            Config.save();
        }
    }

    public static boolean shiftDown() {
        return InputMappings.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)
                || InputMappings.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static boolean ctrlDown() {
        return InputMappings.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)
                || InputMappings.isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL);
    }

    public static boolean altDown() {
        return InputMappings.isKeyDown(GLFW.GLFW_KEY_LEFT_ALT)
                || InputMappings.isKeyDown(GLFW.GLFW_KEY_RIGHT_ALT);
    }
}
