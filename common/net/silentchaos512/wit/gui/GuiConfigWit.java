package net.silentchaos512.wit.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.silentchaos512.wit.WIT;
import net.silentchaos512.wit.config.Config;

public class GuiConfigWit extends GuiConfig {

  public GuiConfigWit(GuiScreen parent) {

    super(parent, Config.getConfigElements(), WIT.MOD_ID, false, false, "WIT Config");
  }

  @Override
  public void initGui() {

    // You can add buttons and initialize fields here
    super.initGui();
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {

    // You can do things like create animations, draw additional elements, etc. here
    super.drawScreen(mouseX, mouseY, partialTicks);
  }

  @Override
  protected void actionPerformed(GuiButton button) {

    // You can process any additional buttons you may have added here
    super.actionPerformed(button);
  }
}
