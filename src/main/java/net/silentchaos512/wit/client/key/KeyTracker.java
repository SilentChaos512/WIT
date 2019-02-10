package net.silentchaos512.wit.client.key;

import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import net.silentchaos512.wit.Wit;
import net.silentchaos512.wit.client.HudRenderObject;
import org.lwjgl.glfw.GLFW;

public final class KeyTracker {
    private static KeyTracker INSTANCE;

    private final FabricKeyBinding toggleHud;
//    private final FabricKeyBinding toggleAdvancedMode;

    private KeyTracker() {
        KeyBindingRegistry.INSTANCE.addCategory(Wit.MOD_NAME);

        toggleHud = FabricKeyBinding.Builder.create(
                new Identifier(Wit.MOD_ID, "toggle_overlay"),
                InputUtil.Type.KEY_KEYBOARD,
                GLFW.GLFW_KEY_KP_1,
                Wit.MOD_NAME
        ).build();
        KeyBindingRegistry.INSTANCE.register(toggleHud);

/*        toggleAdvancedMode = FabricKeyBinding.Builder.create(
                new Identifier(Wit.MOD_ID, "toggle_advanced_info"),
                InputUtil.Type.KEY_KEYBOARD,
                GLFW.GLFW_KEY_KP_4,
                Wit.MOD_NAME
        ).build();
        KeyBindingRegistry.INSTANCE.register(toggleAdvancedMode);*/
    }

    public static void init() {
        if (INSTANCE != null) return;
        INSTANCE = new KeyTracker();

        ClientTickCallback.EVENT.register(client -> {
            if (INSTANCE.toggleHud.wasPressed())
                HudRenderObject.renderHud = !HudRenderObject.renderHud;
        });
    }

    public static boolean shiftDown() {
        return eitherPressed(GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static boolean ctrlDown() {
        return eitherPressed(GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL);
    }

    public static boolean altDown() {
        return eitherPressed(GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_RIGHT_ALT);
    }

    private static boolean eitherPressed(int key1, int key2) {
        long handle = MinecraftClient.getInstance().window.getHandle();
        return InputUtil.isKeyPressed(handle, key1) || InputUtil.isKeyPressed(handle, key2);
    }
}
