package com.stefanparmezan.atomic_parasites.init;

import com.stefanparmezan.atomic_parasites.items.CrashQuestIcon;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;

import java.util.ArrayList;
import java.util.List;

public class InitItems {
    public static final List<Item> ITEMS = new ArrayList<>();

    //Items
    public static final Item CRASH_QUEST_ICON = new CrashQuestIcon("crash_quest_icon");

}
