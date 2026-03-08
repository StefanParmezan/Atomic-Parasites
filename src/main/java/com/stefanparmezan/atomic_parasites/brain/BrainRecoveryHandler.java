package com.stefanparmezan.atomic_parasites.brain;

import net.minecraft.block.Block;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;

public class BrainRecoveryHandler {

    public static float getRecoveryMultiplier(AbstractClientPlayer player) {
        if (player.capabilities.isCreativeMode) return 1.0f;
        float multiplier = 1.0f;
        if (hasFlowersNearby(player)) {
            multiplier += BrainConfig.FLOWER_RECOVERY_BONUS;
        }
        return multiplier;
    }

    private static boolean hasFlowersNearby(AbstractClientPlayer player) {
        BlockPos center = new BlockPos(player);
        int flowerCount = 0;
        int blockLight = player.world.getLightFor(EnumSkyBlock.BLOCK, center);
        if (blockLight < BrainConfig.FLOWER_MIN_LIGHT) return false;

        for (int dx = -BrainConfig.FLOWER_RADIUS; dx <= BrainConfig.FLOWER_RADIUS; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -BrainConfig.FLOWER_RADIUS; dz <= BrainConfig.FLOWER_RADIUS; dz++) {
                    BlockPos check = center.add(dx, dy, dz);
                    if (!player.world.isBlockLoaded(check)) continue;
                    Block block = player.world.getBlockState(check).getBlock();
                    if (block == Blocks.RED_FLOWER || block == Blocks.YELLOW_FLOWER ||
                            block == Blocks.DOUBLE_PLANT || block == Blocks.FLOWER_POT) {
                        flowerCount++;
                    }
                }
            }
        }
        return flowerCount >= BrainConfig.FLOWER_MIN_COUNT;
    }

    public static String getRecoveryStatus(AbstractClientPlayer player) {
        float mult = getRecoveryMultiplier(player);
        if (mult >= 1.0f + BrainConfig.FLOWER_RECOVERY_BONUS) return "🌸 Цветы + Свет";
        return "⚪ Обычное";
    }
}