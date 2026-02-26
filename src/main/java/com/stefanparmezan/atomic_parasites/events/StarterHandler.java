package com.stefanparmezan.atomic_parasites.events;

import com.stefanparmezan.atomic_parasites.init.InitItems;
import com.stefanparmezan.atomic_parasites.main.AtomicParasitesInfo;
import com.stefanparmezan.atomic_parasites.utils.helpers.HbmHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = AtomicParasitesInfo.MOD_ID)
public class StarterHandler {

    // Копируем флаг из умершего игрока в нового при возрождении
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        boolean flag = event.getOriginal().getEntityData().getBoolean("atomic_parasites:armor_received");
        event.getEntityPlayer().getEntityData().setBoolean("atomic_parasites:armor_received", flag);
    }

    // Выдаём предметы только если флаг ещё не установлен
    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote) return;
        if (!(event.getEntity() instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) event.getEntity();

        if (!player.getEntityData().getBoolean("atomic_parasites:armor_received")) {
            player.inventory.armorInventory.set(3, new ItemStack(InitItems.ENGINEER_WELDER_MASK));
            player.inventory.armorInventory.set(2, new ItemStack(InitItems.ENGINEER_JACKET));
            player.inventory.armorInventory.set(1, new ItemStack(InitItems.ENGINEER_PANTS));
            player.inventory.armorInventory.set(0, new ItemStack(InitItems.ENGINEER_BOOTS));
            player.inventory.addItemStackToInventory(new ItemStack(InitItems.ENGINEER_TOOL_CASE));

            Object gun = HbmHelper.getHbmItem("gun_fireext");
            Object ammo = HbmHelper.getHbmItem("ammo_fireext");


            if (gun != null) {
                player.inventory.addItemStackToInventory(((net.minecraft.item.Item) gun).getDefaultInstance());
            }

            if (ammo != null) {
                player.inventory.addItemStackToInventory(((net.minecraft.item.Item) ammo).getDefaultInstance());
            }

            player.getEntityData().setBoolean("atomic_parasites:armor_received", true);
        }
    }
}