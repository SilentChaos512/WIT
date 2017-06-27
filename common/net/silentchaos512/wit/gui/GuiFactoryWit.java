package net.silentchaos512.wit.gui;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

public class GuiFactoryWit implements IModGuiFactory {

  @Override
  public void initialize(Minecraft minecraftInstance) {

  }

  public Class<? extends GuiScreen> mainConfigGuiClass() {

    return GuiConfigWit.class;
  }

  @Override
  public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {

    return new HashSet<>();
  }

  @Override
  public boolean hasConfigGui() {

    return true;
  }

  @Override
  public GuiScreen createConfigGui(GuiScreen parentScreen) {

    return new GuiConfigWit(parentScreen);
  }
}
