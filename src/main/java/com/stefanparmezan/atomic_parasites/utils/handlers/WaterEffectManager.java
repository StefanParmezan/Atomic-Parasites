package com.stefanparmezan.atomic_parasites.utils.handlers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.math.BlockPos;

public class WaterEffectManager {

    private static boolean isInWater = false;

    public static void update(AbstractClientPlayer player) {
        // Проверяем блок на уровне головы игрока (Y + 1.2)
        BlockPos headPos = new BlockPos(player.posX, player.posY + 1.6, player.posZ);

        // Проверяем, загружен ли чанк и является ли блок жидкостью
        if (player.world.isBlockLoaded(headPos)) {
            isInWater = player.world.getBlockState(headPos).getMaterial().isLiquid();
        } else {
            isInWater = false;
        }
    }

    public static boolean isPlayerInWater() {
        return isInWater;
    }

    public static void reset() {
        isInWater = false;
    }
}