package com.stefanparmezan.atomic_parasites.items;

import com.stefanparmezan.atomic_parasites.init.InitItems;
import com.stefanparmezan.atomic_parasites.main.AtomicParasites;
import com.stefanparmezan.atomic_parasites.main.AtomicParasitesInfo;
import com.stefanparmezan.atomic_parasites.utils.interfaces.IHasModel;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CrashQuestIcon extends Item implements IHasModel {
    public CrashQuestIcon(String name) {
        setRegistryName(name);
        setTranslationKey(AtomicParasitesInfo.TRANSLATION_KEY + name);

        InitItems.ITEMS.add(this);
        AtomicParasites.LOGGER.info("Items list: {}", InitItems.ITEMS);
    }

    @Override
    public void registerModels() {
        AtomicParasites.proxy.registerItemRenderer(this, 0, "inventory");
    }


}
