package com.tlp.srptweaks;

import zone.rong.mixinbooter.ILateMixinLoader;
import zone.rong.mixinbooter.MixinLoader;

import java.util.ArrayList;
import java.util.List;

@MixinLoader
public class SRPTweaksSetupLate implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        List<String> mixins = new ArrayList<>();
        mixins.add("mixins.srptweaks.late.json");

        return mixins;
    }
}
