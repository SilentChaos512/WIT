package net.silentchaos512.wit;

import net.fabricmc.api.ClientModInitializer;
import net.silentchaos512.wit.client.key.KeyTracker;

public class WitClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KeyTracker.init();
        // TODO: TooltipHandler
    }
}
