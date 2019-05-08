package net.silentchaos512.wit;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.silentchaos512.wit.client.TooltipHandler;
import net.silentchaos512.wit.client.key.KeyTracker;
import net.silentchaos512.wit.config.Config;

class SideProxy {
    SideProxy() {
        Config.init();

        IEventBus lifeCycleBus = FMLJavaModLoadingContext.get().getModEventBus();
        lifeCycleBus.addListener(this::commonSetup);
        lifeCycleBus.addListener(this::imcEnqueue);
        lifeCycleBus.addListener(this::imcProcess);
    }

    private void commonSetup(FMLCommonSetupEvent event) { }

    private void imcEnqueue(InterModEnqueueEvent event) { }

    private void imcProcess(InterModProcessEvent event) { }

    static class Client extends SideProxy {
        Client() {
            KeyTracker.init();
            TooltipHandler.init();

            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        }

        private void clientSetup(FMLClientSetupEvent event) { }
    }

    static class Server extends SideProxy {
        Server() {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverSetup);
        }

        private void serverSetup(FMLDedicatedServerSetupEvent event) { }
    }
}
