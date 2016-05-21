package net.silentchaos512.wit.client.key;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.silentchaos512.wit.WIT;
import net.silentchaos512.wit.client.HudRenderObject;
import net.silentchaos512.wit.config.Config;

public class KeyTracker {

  public static KeyTracker instance = new KeyTracker();

  public KeyBinding toggleHud;
  public KeyBinding toggleAdvancedMode;

  public KeyTracker() {

    toggleHud = new KeyBinding("Toggle WIT HUD", Keyboard.KEY_NUMPAD1, WIT.MOD_NAME);
    ClientRegistry.registerKeyBinding(toggleHud);
    toggleAdvancedMode = new KeyBinding("Toggle Advanced Mode", Keyboard.KEY_NUMPAD4, WIT.MOD_NAME);
    ClientRegistry.registerKeyBinding(toggleAdvancedMode);
  }

  public static void init() {

    MinecraftForge.EVENT_BUS.register(instance);
  }

  @SubscribeEvent
  public void onKeyInput(KeyInputEvent event) {

    if (toggleHud.isPressed()) {
      HudRenderObject.renderHud = !HudRenderObject.renderHud;
    } else if (toggleAdvancedMode.isPressed()) {
      Config.hudAdvancedMode = !Config.hudAdvancedMode;
      Config.save();
    }
  }

  public boolean isShiftPressed() {

    return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
  }

  public boolean isControlPressed() {

    return Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
  }
}
