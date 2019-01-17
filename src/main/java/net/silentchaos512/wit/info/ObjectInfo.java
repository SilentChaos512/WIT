package net.silentchaos512.wit.info;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.silentchaos512.wit.api.IInfoObject;
import net.silentchaos512.wit.api.WitHudInfoEvent;

import javax.annotation.Nullable;
import java.util.List;

public abstract class ObjectInfo implements IInfoObject {
    private final ResourceLocation name;
    private final ModContainer mod; // TODO: Is this needed?
    private final String modName;

    ObjectInfo(ResourceLocation name) {
        this.name = name;
        this.mod = ModList.get().getModContainerById(name.getNamespace()).orElse(null);
        this.modName = this.mod != null ? mod.getModInfo().getDisplayName() : "Minecraft";
    }

    public ResourceLocation getName() {
        return name;
    }

    public String getModName() {
        return modName;
    }

    @Nullable
    public ModContainer getMod() {
        return mod;
    }

    ITextComponent displayModName() {
        return new TextComponentString(modName);
    }

    ITextComponent displayRegistryName() {
        return new TextComponentString(name.toString());
    }

    static void processInfoEvent(List<ITextComponent> lines, WitHudInfoEvent event) {
        if (!MinecraftForge.EVENT_BUS.post(event)) {
            lines.addAll(event.getLines());
        }
    }
}
