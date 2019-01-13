package net.silentchaos512.wit.client.key;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public final class KeyTracker {
    public static KeyTracker INSTANCE = new KeyTracker();

    private KeyBinding toggleHud;
    private KeyBinding toggleAdvancedMode;

    private KeyTracker() {
//        toggleHud = new KeyBinding("Toggle WIT HUD", Keyboard.KEY_NUMPAD1, WIT.MOD_NAME);
//        ClientRegistry.registerKeyBinding(toggleHud);
//        toggleAdvancedMode = new KeyBinding("Toggle Advanced Mode", Keyboard.KEY_NUMPAD4, WIT.MOD_NAME);
//        ClientRegistry.registerKeyBinding(toggleAdvancedMode);

        MinecraftForge.EVENT_BUS.addListener(this::onKeyInput);
    }

    private void onKeyInput(KeyInputEvent event) {
//        if (toggleHud.isPressed()) {
//            HudRenderObject.renderHud = !HudRenderObject.renderHud;
//        } else if (toggleAdvancedMode.isPressed()) {
//            Config.hudAdvancedMode = !Config.hudAdvancedMode;
//            Config.save();
//        }
    }

    public boolean isShiftPressed() {
//        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
        return false;
    }

    public boolean isControlPressed() {
//        return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
        return false;
    }
}
