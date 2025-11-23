package com.tlp.srptweaks.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class InvasionSaveData extends WorldSavedData {
    private static final boolean debugMode = ModConfigManager.debug;
    private static final String DATA_NAME = "srptweaks_invasion"; // internal ID
    private static InvasionSaveData instance;

    private boolean invasionEnded = false;

    public InvasionSaveData() {
        super(DATA_NAME);
    }

    public InvasionSaveData(String name) {
        super(name);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.invasionEnded = nbt.getBoolean("InvasionEnded");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("InvasionEnded", this.invasionEnded);
        return compound;
    }

    public boolean getInvasionEnded() {
        return invasionEnded;
    }

    public void setInvasionEnded(boolean invasionEnded) {
        this.invasionEnded = invasionEnded;
        this.markDirty();
        if (debugMode) {
            System.out.println("SRPTweaks marked invasion as ended");
        }
    }

    public static InvasionSaveData get(World world) {
        MapStorage storage = world.getMapStorage();
        instance = (InvasionSaveData) storage.getOrLoadData(InvasionSaveData.class, DATA_NAME);

        if (instance == null) {
            instance = new InvasionSaveData(DATA_NAME);
            storage.setData(DATA_NAME, instance);
            instance.markDirty();
        }
        if (debugMode) {
            System.out.println("SRPTweaks returning instance of InvasionSaveData");
        }
        return instance;
    }
}
