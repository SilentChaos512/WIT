package net.silentchaos512.wit.gui;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;


public class GuiFactoryWit implements IModGuiFactory {

  @Override
  public void initialize(Minecraft minecraftInstance) {

  }

  @Override
  public Class<? extends GuiScreen> mainConfigGuiClass() {

    return GuiConfigWit.class;
  }

  @Override
  public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {

    return null;
  }

  @Override
  public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {

    return null;
  }
}
