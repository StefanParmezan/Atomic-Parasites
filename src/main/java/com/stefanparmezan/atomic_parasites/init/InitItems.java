package com.stefanparmezan.atomic_parasites.init;

import com.stefanparmezan.atomic_parasites.armor.EngineerArmor;
import com.stefanparmezan.atomic_parasites.items.CrashQuestIcon;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InitItems {
    public static final List<Item> ITEMS = new ArrayList<>();

    public static final Item CRASH_QUEST_ICON = new CrashQuestIcon("crash_quest_icon");

    public static ItemArmor.ArmorMaterial ARMOR_MATERIAL_ENGINEER = Objects.requireNonNull(
            EnumHelper.addArmorMaterial(
                    "atomic_parasites:engineer",
                    "atomic_parasites:engineer",
                    15,
                    new int[]{1, 5, 6, 2},
                    11,
                    SoundEvents.ITEM_ARMOR_EQUIP_IRON,
                    0.0F
            )
    ).setRepairItem(new ItemStack(Items.LEATHER));

    public static Item
            ENGINEER_BOOTS = new EngineerArmor("engineer_boots", ARMOR_MATERIAL_ENGINEER, 1, EntityEquipmentSlot.FEET),
            ENGINEER_PANTS = new EngineerArmor("engineer_pants", ARMOR_MATERIAL_ENGINEER, 2, EntityEquipmentSlot.LEGS),
            ENGINEER_JACKET = new EngineerArmor("engineer_jacket", ARMOR_MATERIAL_ENGINEER, 1, EntityEquipmentSlot.CHEST),
            ENGINEER_WELDER_MASK = new EngineerArmor("engineer_welder_mask", ARMOR_MATERIAL_ENGINEER, 1, EntityEquipmentSlot.HEAD);
}