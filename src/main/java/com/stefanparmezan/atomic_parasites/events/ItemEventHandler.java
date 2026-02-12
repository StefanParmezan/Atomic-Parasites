package com.stefanparmezan.atomic_parasites.events;

import com.stefanparmezan.atomic_parasites.init.InitItems;
import com.stefanparmezan.atomic_parasites.main.AtomicParasitesInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.hbm.items.ModItems;

@Mod.EventBusSubscriber(modid = AtomicParasitesInfo.MOD_ID)
public class ItemEventHandler {

    @SubscribeEvent
    public static void onToolBoxRightClick(PlayerInteractEvent.RightClickItem event) {
        if (event.getWorld().isRemote) return;
        EntityPlayer player = event.getEntityPlayer();
        ItemStack held = event.getItemStack();

        if (ItemStack.areItemsEqual(held, new ItemStack(InitItems.ENGINEER_TOOL_CASE))) {
            // Выдаём предметы HBM напрямую
            player.inventory.addItemStackToInventory(new ItemStack(Items.IRON_PICKAXE));
            player.inventory.addItemStackToInventory(new ItemStack(ModItems.crowbar));
            player.inventory.addItemStackToInventory(new ItemStack(ModItems.wrench));
            player.inventory.addItemStackToInventory(new ItemStack(ModItems.template_folder));
            player.inventory.addItemStackToInventory(new ItemStack(ModItems.static_sandwich));
            player.inventory.addItemStackToInventory(new ItemStack(ModItems.ducttape));

            held.shrink(1);
            event.setCanceled(true);
        }
    }
}