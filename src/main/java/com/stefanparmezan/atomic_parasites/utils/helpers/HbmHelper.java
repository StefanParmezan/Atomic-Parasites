package com.stefanparmezan.atomic_parasites.utils.helpers;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class HbmHelper {


    // Вспомогательный метод для безопасного получения предмета по имени
    public static Item getHbmItem(String name) {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("hbm", name));
        if (item == null) {
            System.out.println("[Atomic Parasites] Warning: HBM item not found: " + name);
        }
        return item;
    }
}
