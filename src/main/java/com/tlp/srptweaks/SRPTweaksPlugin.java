package com.tlp.srptweaks;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.mixin.Mixins;
import zone.rong.mixinbooter.IEarlyMixinLoader;
import zone.rong.mixinbooter.MixinBooterPlugin;

import java.util.Collections;
import java.util.Map;
import java.util.List;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.Name("SRPTweaksPlugin")
public class SRPTweaksPlugin implements IFMLLoadingPlugin, IEarlyMixinLoader {

    @Override
    public String[] getASMTransformerClass() { return new String[0]; }

    @Override
    public String getModContainerClass() { return null; }

    @Override
    public String getSetupClass() { return null; }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() { return null; }

    @Override
    public List<String> getMixinConfigs() {
        return Collections.singletonList("mixins.srptweaks.early.json");
    }
}
