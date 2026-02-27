package com.stefanparmezan.atomic_parasites.events;

import com.stefanparmezan.atomic_parasites.main.AtomicParasites;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class PlayerBlockEventHandler {

    private static final int FIRE_TIME = 2;

    private static final String[] FIRE_EXTINGUISHER_ITEMS = {
            "minecraft:water_bucket"
    };

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getHand() != EnumHand.MAIN_HAND) return;
        if (event.getWorld().isRemote) return;

        EntityPlayer player = event.getEntityPlayer();
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        EnumFacing face = event.getFace();

        // 🔹 Проверяем, держит ли игрок предмет для тушения
        if (isFireExtinguisherItem(player.getHeldItemMainhand())) {
            AtomicParasites.LOGGER.info("[DEBUG] 🔧 Предмет-огнетушитель: огонь можно тушить");
            return; // Пропускаем логику поджога, даём ванильному поведению сработать
        }

        if (checkAndIgnite(world, player, pos, event)) return;
        if (face != null && checkAndIgnite(world, player, pos.offset(face), event)) return;

        AtomicParasites.LOGGER.info("[DEBUG] Клик мимо огня: {}", pos);
    }

    // 🔹 Проверка: является ли предмет разрешённым огнетушителем
    private static boolean isFireExtinguisherItem(net.minecraft.item.ItemStack stack) {
        if (stack.isEmpty()) return false;

        Item item = stack.getItem();
        if (item == null) return false;

        ResourceLocation registryName = item.getRegistryName();
        if (registryName == null) return false;

        String itemName = registryName.toString(); // например: "minecraft:water_bucket"

        for (String allowed : FIRE_EXTINGUISHER_ITEMS) {
            if (itemName.equalsIgnoreCase(allowed)) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkAndIgnite(World world, EntityPlayer player, BlockPos pos, PlayerInteractEvent.LeftClickBlock event) {
        Block block = world.getBlockState(pos).getBlock();

        if (block == Blocks.FIRE || block instanceof BlockFire) {
            AtomicParasites.LOGGER.info("[DEBUG] 🔥 ОГОНЬ НАЙДЕН в {}: Поджигаем игрока!", pos);

            event.setUseBlock(Event.Result.DENY);
            event.setUseItem(Event.Result.DENY);
            event.setCanceled(true);

            player.setFire(FIRE_TIME);
            world.setBlockState(pos, Blocks.FIRE.getDefaultState());

            return true;
        }
        return false;
    }
}