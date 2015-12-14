package net.silentchaos512.wit.proxy;

import net.silentchaos512.wit.client.key.KeyTracker;

public class ClientProxy extends CommonProxy {
  
  @Override
  public void preInit() {
    
    super.preInit();
  }
  
  @Override
  public void init() {

    super.init();
  }
  
  @Override
  public void postInit() {
    
    super.postInit();
  }

  @Override
  public void registerKeyHandlers() {
    
    KeyTracker.init();
  }
}
