package com.stefanparmezan.atomic_parasites.brain;

import com.stefanparmezan.atomic_parasites.main.AtomicParasites;
import net.minecraft.block.Block;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;

public class BrainRecoveryHandler {

    // === ⚙️ НАСТРОЙКИ ===
    private static final int FLOWER_RADIUS = 5;
    private static final int MIN_FLOWERS = 2;
    private static final float FLOWER_BONUS = 0.30f;
    private static final int MIN_LIGHT_FOR_FLOWERS = 8;

    // === 📊 ПОЛУЧИТЬ МНОЖИТЕЛЬ ВОССТАНОВЛЕНИЯ ===
    public static float getRecoveryMultiplier(AbstractClientPlayer player) {
        if (player.capabilities.isCreativeMode) return 1.0f;

        float multiplier = 1.0f;

        if (hasFlowersNearby(player)) {
            multiplier += FLOWER_BONUS;
        }

        return multiplier;
    }

    // === 🌸 ПРОВЕРКА: есть ли цветы поблизости + свет ===
    private static boolean hasFlowersNearby(AbstractClientPlayer player) {
        BlockPos center = new BlockPos(player);
        int flowerCount = 0;

        // Проверяем только искусственный свет (работает под землёй)
        int blockLight = player.world.getLightFor(EnumSkyBlock.BLOCK, center);
        if (blockLight < MIN_LIGHT_FOR_FLOWERS) return false;

        for (int dx = -FLOWER_RADIUS; dx <= FLOWER_RADIUS; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -FLOWER_RADIUS; dz <= FLOWER_RADIUS; dz++) {
                    BlockPos check = center.add(dx, dy, dz);
                    if (!player.world.isBlockLoaded(check)) continue;

                    Block block = player.world.getBlockState(check).getBlock();

                    // ТОЛЬКО ЦВЕТЫ И ГОРШКИ
                    if (block == Blocks.RED_FLOWER ||
                            block == Blocks.YELLOW_FLOWER ||
                            block == Blocks.DOUBLE_PLANT ||
                            block == Blocks.FLOWER_POT) {
                        flowerCount++;
                    }
                }
            }
        }

        return flowerCount >= MIN_FLOWERS;
    }

    // === 📊 ОПИСАНИЕ СТАТУСА ===
    public static String getRecoveryStatus(AbstractClientPlayer player) {
        float mult = getRecoveryMultiplier(player);
        if (mult >= 1.3f) return "🌸 Цветы + Свет";
        return "⚪ Обычное";
    }
}