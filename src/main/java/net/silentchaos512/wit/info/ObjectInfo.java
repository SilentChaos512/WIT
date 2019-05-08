package net.silentchaos512.wit.info;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.silentchaos512.wit.api.IInfoObject;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class ObjectInfo implements IInfoObject {
    private static final Map<String, ModContainer> MODS_BY_ID = new HashMap<>();
    private final ResourceLocation name;
    private final String modName;

    ObjectInfo(ResourceLocation name) {
        this.name = name;
        ModContainer mod = getModContainer(name);
        this.modName = mod != null ? mod.getModInfo().getDisplayName(): "Minecraft";
    }

    @Nullable
    private static ModContainer getModContainer(ResourceLocation objId) {
        String modId = objId.getNamespace();
        if ("minecraft".equals(modId)) return null;

        if (MODS_BY_ID.containsKey(modId)) {
            return MODS_BY_ID.get(modId);
        }

        // Haven't seen this mod yet, look for it then add to the map.
        ModContainer mod = ModList.get().getModContainerById(modId).orElseThrow(() ->
                new IllegalStateException("Unknown mod ID: '" + modId + "' for object '" + objId + "'"));
        MODS_BY_ID.put(modId, mod);
        return mod;
    }

    public ResourceLocation getName() {
        return name;
    }

    public String getModName() {
        return modName;
    }

    ITextComponent displayModName() {
        return new TextComponentString(modName);
    }

    ITextComponent displayRegistryName() {
        return new TextComponentString(name.toString());
    }

}
