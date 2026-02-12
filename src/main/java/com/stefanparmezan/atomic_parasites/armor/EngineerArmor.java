package com.stefanparmezan.atomic_parasites.armor;

import com.stefanparmezan.atomic_parasites.init.InitItems;
import com.stefanparmezan.atomic_parasites.main.AtomicParasites;
import com.stefanparmezan.atomic_parasites.main.AtomicParasitesInfo;
import com.stefanparmezan.atomic_parasites.utils.interfaces.IHasModel;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;

public class EngineerArmor extends ItemArmor implements IHasModel {

    public EngineerArmor(String name, ArmorMaterial armorMaterial, int renderIndexIn, EntityEquipmentSlot entityEquipmentSlot){
        super(armorMaterial, renderIndexIn, entityEquipmentSlot);
        this.setRegistryName(name);
        setTranslationKey(AtomicParasitesInfo.TRANSLATION_KEY + name);
        setCreativeTab(AtomicParasites.creativeTab);
        InitItems.ITEMS.add(this);
    }

    @Override
    public void registerModels() {
        AtomicParasites.proxy.registerItemRenderer(this, 0, "inventory");
    }
}
