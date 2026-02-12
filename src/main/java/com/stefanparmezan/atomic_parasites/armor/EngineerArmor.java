package com.stefanparmezan.atomic_parasites.armor;

import com.stefanparmezan.atomic_parasites.main.AtomicParasites;
import com.stefanparmezan.atomic_parasites.main.AtomicParasitesInfo;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;

public class EngineerArmor extends ItemArmor {

    public EngineerArmor(String name, ArmorMaterial armorMaterial, int renderIndexIn, EntityEquipmentSlot entityEquipmentSlot){
        super(armorMaterial, renderIndexIn, entityEquipmentSlot);
        this.setRegistryName(name);
        setTranslationKey(AtomicParasitesInfo.TRANSLATION_KEY + name);
        setCreativeTab(AtomicParasites.creativeTab);
    }
}
