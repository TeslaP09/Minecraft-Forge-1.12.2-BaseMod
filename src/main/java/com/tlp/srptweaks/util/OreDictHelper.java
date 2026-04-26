package com.tlp.srptweaks.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictHelper {

    public static boolean hasOreDictName(ItemStack stack, String oreDictName) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        int targetId = OreDictionary.getOreID(oreDictName);
        int[] ids = OreDictionary.getOreIDs(stack);

        for (int id : ids) {
            if (id == targetId) {
                return true;
            }
        }
        return false;
    }
}
