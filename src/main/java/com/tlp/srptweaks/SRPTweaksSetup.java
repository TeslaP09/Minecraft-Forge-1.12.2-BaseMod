package com.tlp.srptweaks;

import zone.rong.mixinbooter.ILateMixinLoader;

import java.util.ArrayList;
import java.util.List;

public class SRPTweaksSetup implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        List<String> mixins = new ArrayList<>();
        mixins.add("mixins.srptweaks.json");

        return mixins;
    }
}
