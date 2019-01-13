package net.silentchaos512.wit.info;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModContainer;
import net.silentchaos512.wit.WIT;
import net.silentchaos512.wit.api.IInfoObject;
import net.silentchaos512.wit.api.WitHudInfoEvent;
import net.silentchaos512.wit.config.Config;

import javax.annotation.Nullable;
import java.util.List;

public abstract class ObjectInfo implements IInfoObject {
    private final ResourceLocation name;
    private final ModContainer mod;
    private final String modName;

    ObjectInfo(ResourceLocation name) {
        this.name = name;
        this.mod = WIT.INSTANCE.mods.get(this.name.getNamespace());
        this.modName = this.mod != null ? mod.getModInfo().getDisplayName() : "Minecraft";
    }

    public ResourceLocation getName() {
        return this.name;
    }

    public String getModName() {
        return this.modName;
    }

    @Nullable
    public ModContainer getMod() {
        return this.mod;
    }

    ITextComponent displayModName() {
        return Config.hudModName.format(this.modName);
    }

    ITextComponent displayRegistryName() {
        return Config.hudResourceName.format(this.name.toString());
    }

    static void processInfoEvent(List<ITextComponent> lines, WitHudInfoEvent event) {
        if (!MinecraftForge.EVENT_BUS.post(event)) {
            lines.addAll(event.getLines());
        }
    }
}
