package net.silentchaos512.wit;

import net.fabricmc.api.ModInitializer;
import net.silentchaos512.wit.api.WitBlockReplacements;
import net.silentchaos512.wit.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Wit implements ModInitializer {
    public static final String MOD_ID = "wit";
    public static final String MOD_NAME = "Wit: Fabric Edition";

    public static Logger LOGGER = LogManager.getLogger(MOD_NAME);

    @Override
    public void onInitialize() {
        Config.init();
        WitBlockReplacements.init();
    }
}
