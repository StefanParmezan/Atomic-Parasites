package com.stefanparmezan.atomic_parasites.items;

import com.stefanparmezan.atomic_parasites.init.InitItems;
import com.stefanparmezan.atomic_parasites.main.AtomicParasites;
import com.stefanparmezan.atomic_parasites.main.AtomicParasitesInfo;
import com.stefanparmezan.atomic_parasites.utils.interfaces.IHasModel;
import net.minecraft.item.Item;

public class EngineerToolCase extends Item implements IHasModel {
    public EngineerToolCase(String name) {
        setRegistryName(name);
        setTranslationKey(AtomicParasitesInfo.TRANSLATION_KEY + name);
        setMaxDamage(2);          // damage может быть 0, 1, 2
        setMaxStackSize(1);       // предмет не стакается, чтобы анимация не путалась
        InitItems.ITEMS.add(this);
        AtomicParasites.LOGGER.info("Items list: {}", InitItems.ITEMS);
        setCreativeTab(AtomicParasites.creativeTab);

    }

    @Override
    public void registerModels() {
        AtomicParasites.proxy.registerItemRenderer(this, 0, "inventory");
    }
}