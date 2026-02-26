package com.stefanparmezan.atomic_parasites.events;

// import net.minecraftforge.fml.common.Mod;  ← можно удалить
import com.stefanparmezan.atomic_parasites.main.AtomicParasites;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerBlockEventHandler {

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getHand() != EnumHand.MAIN_HAND) return;
        if (event.getWorld().isRemote) return;

        EntityPlayer player = event.getEntityPlayer();
        World world = event.getWorld();
        BlockPos pos = event.getPos();

        // Получаем блок, по которому кликнули
        Block clickedBlock = world.getBlockState(pos).getBlock();

        // 🔹 Если клик по огню — всё просто
        if (clickedBlock == Blocks.FIRE || clickedBlock instanceof BlockFire) {
            event.setCanceled(true);
            player.setFire(4);
            AtomicParasites.LOGGER.info("[DEBUG] 🔥 Клик по огню! Игрок подожжен");
            return;
        }

        // 🔹 Если клик по блоку ПОД огнём — проверяем, есть ли огонь сверху
        BlockPos firePos = pos.up();
        Block blockAbove = world.getBlockState(firePos).getBlock();

        if (blockAbove == Blocks.FIRE || blockAbove instanceof BlockFire) {
            event.setCanceled(true);
            player.setFire(3);
            // Принудительно восстанавливаем огонь, если он успел погаснуть
            world.setBlockState(firePos, Blocks.FIRE.getDefaultState());
            AtomicParasites.LOGGER.info("[DEBUG] 🔥 Огонь над блоком! Игрок подожжен, огонь восстановлен");
            return;
        }

        AtomicParasites.LOGGER.info("[DEBUG] Клик по блоку: {}, огня нет", clickedBlock.getRegistryName());
    }
}