package net.silentchaos512.wit.info;

import net.fabricmc.loader.FabricLoader;
import net.fabricmc.loader.ModContainer;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Identifier;
import net.silentchaos512.wit.api.IInfoObject;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class ObjectInfo implements IInfoObject {
    private static final Map<String, ModContainer> MODS_BY_ID = new HashMap<>();
    private final Identifier name;
    private final String modName;

    ObjectInfo(Identifier name) {
        this.name = name;
        ModContainer mod = getModContainer(name);
        this.modName = mod != null ? mod.getInfo().getName() : "Minecraft";
    }

    @Nullable
    private static ModContainer getModContainer(Identifier objId) {
        String modId = objId.getNamespace();
        if ("minecraft".equals(modId)) return null;

        if (MODS_BY_ID.containsKey(modId)) {
            return MODS_BY_ID.get(modId);
        }

        // Haven't seen this mod yet, look for it then add to the map.
        // TODO: Looks like Fabric intends to change this, so expect this to break!
        for (ModContainer mod : FabricLoader.INSTANCE.getModContainers()) {
            if (mod.getInfo().getId().equals(modId)) {
                MODS_BY_ID.put(modId, mod);
                return mod;
            }
        }
        // Something has gone horribly wrong...
        throw new IllegalStateException("Unknown mod ID: '" + modId + "' for object '" + objId + "'");
    }

    public Identifier getName() {
        return name;
    }

    public String getModName() {
        return modName;
    }

    TextComponent displayModName() {
        return new StringTextComponent(modName);
    }

    TextComponent displayRegistryName() {
        return new StringTextComponent(name.toString());
    }

}
