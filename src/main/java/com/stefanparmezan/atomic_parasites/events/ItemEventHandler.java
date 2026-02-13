package com.stefanparmezan.atomic_parasites.events;

import com.stefanparmezan.atomic_parasites.init.InitItems;
import com.stefanparmezan.atomic_parasites.main.AtomicParasitesInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import com.hbm.items.ModItems;

@Mod.EventBusSubscriber(modid = AtomicParasitesInfo.MOD_ID)
public class ItemEventHandler {

    private static int ticksAnimationPeriods = 14;

    @SubscribeEvent
    public static void onToolBoxRightClick(PlayerInteractEvent.RightClickItem event) {
        if (event.getWorld().isRemote) return;

        EntityPlayer player = event.getEntityPlayer();
        ItemStack held = event.getItemStack();

        // Проверяем, что это наш инструментальный ящик и он не в процессе анимации
        if (held.getItem() == InitItems.ENGINEER_TOOL_CASE && held.getItemDamage() == 0 && !held.hasTagCompound()) {
            startAnimation(player, held, event.getWorld().getTotalWorldTime());
            event.setCanceled(true); // предотвращаем стандартное использование
        }
    }

    private static void startAnimation(EntityPlayer player, ItemStack stack, long currentTime) {
        // Первый этап: damage = 1 (модель _45)
        stack.setItemDamage(1);

        NBTTagCompound nbt = stack.getOrCreateSubCompound("animation");
        nbt.setLong("nextStage", currentTime + ticksAnimationPeriods);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Работаем только на сервере и в конце тика, чтобы не мешать другим обработчикам
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote) return;

        EntityPlayer player = event.player;
        ItemStack held = player.getHeldItemMainhand();

        // Проверяем, что в руке наш предмет и есть данные анимации
        if (held.getItem() != InitItems.ENGINEER_TOOL_CASE) return;
        if (!held.hasTagCompound() || !held.getTagCompound().hasKey("animation")) return;

        NBTTagCompound animTag = held.getTagCompound().getCompoundTag("animation");
        if (!animTag.hasKey("nextStage")) return;

        long nextStage = animTag.getLong("nextStage");
        long currentTime = player.world.getTotalWorldTime();

        if (currentTime >= nextStage) {
            int damage = held.getItemDamage();

            if (damage == 1) {
                // Переход ко второму этапу: damage = 2 (модель _90)
                held.setItemDamage(2);
                animTag.setLong("nextStage", currentTime + ticksAnimationPeriods);
                held.getTagCompound().setTag("animation", animTag);
            }
            else if (damage == 2) {
                // Анимация завершена – выдаём предметы и убираем один ящик
                // Сначала сбрасываем состояние для оставшихся предметов в стаке
                held.setItemDamage(0);
                held.getTagCompound().removeTag("animation");
                if (held.getTagCompound().isEmpty()) {
                    held.setTagCompound(null);
                }

                // Выдаём предметы HBM
                player.inventory.addItemStackToInventory(new ItemStack(Items.IRON_PICKAXE));
                player.inventory.addItemStackToInventory(new ItemStack(ModItems.crowbar));
                player.inventory.addItemStackToInventory(new ItemStack(ModItems.wrench));
                player.inventory.addItemStackToInventory(new ItemStack(ModItems.template_folder));
                player.inventory.addItemStackToInventory(new ItemStack(ModItems.static_sandwich));
                player.inventory.addItemStackToInventory(new ItemStack(ModItems.ducttape));

                // Уменьшаем количество использованных ящиков на 1
                held.shrink(1);
            }
        }
    }
}