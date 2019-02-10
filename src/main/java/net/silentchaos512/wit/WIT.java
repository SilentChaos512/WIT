package net.silentchaos512.wit;

import net.fabricmc.api.ModInitializer;
import net.silentchaos512.wit.api.InfoCallbacks;
import net.silentchaos512.wit.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Wit implements ModInitializer {
    public static final String MOD_ID = "wit";
    public static final String MOD_NAME = "WIT: Fabric Edition";

    public static Logger LOGGER = LogManager.getLogger(MOD_NAME);

    @Override
    public void onInitialize() {
        Config.init();

        InfoCallbacks.registerBlockHandler(event -> event.add("block callback"));
        InfoCallbacks.registerItemHandler(event -> event.add("item callback"));
        InfoCallbacks.registerEntityHandler(event -> event.add("entity callback"));
    }
}
