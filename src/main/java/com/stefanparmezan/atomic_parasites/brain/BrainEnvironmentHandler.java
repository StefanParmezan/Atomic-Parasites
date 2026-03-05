package com.stefanparmezan.atomic_parasites.brain;

import com.stefanparmezan.atomic_parasites.main.AtomicParasites;
import net.minecraft.block.Block;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class BrainEnvironmentHandler {

    private static int darknessTimer = 0;
    private static int recoveryTimer = 0;

    public static void tick(AbstractClientPlayer player) {
        if (player.capabilities.isCreativeMode) { resetTimers(); return; }

        boolean night = isNightTime(player.world);
        BlockPos pos = new BlockPos(player.posX, player.posY + 1.6, player.posZ);
        boolean canSeeSky = player.world.canSeeSky(pos);
        boolean underSolidCover = isUnderSolidCover(player);
        int blockLight = player.world.getLightFor(EnumSkyBlock.BLOCK, pos);
        float sanity = BrainManager.getCurrentSanity();

        // === 🌑 ПАДЕНИЕ: ночь + нет надёжного укрытия + нет яркого света ===
        if (night && !underSolidCover && blockLight <= BrainConfig.DARKNESS_LIGHT_THRESHOLD) {
            recoveryTimer = 0; darknessTimer++;
            if (darknessTimer >= BrainConfig.ENV_DECAY_INTERVAL && sanity > 0) {
                BrainManager.addSanity(-1); darknessTimer = 0;
                AtomicParasites.LOGGER.info("[Brain] 📉 Sanity -1 (Night+Exposed) | Light: {} | New: {}",
                        blockLight, (int)BrainManager.getCurrentSanity());
            }
        }
        // === ☀️ ВОССТАНОВЛЕНИЕ: день ИЛИ яркий источник света ===
        // ⚠️ underSolidCover НЕ даёт восстановления — только яркий свет!
        else if (!night || blockLight >= BrainConfig.BRIGHT_LIGHT_THRESHOLD) {
            darknessTimer = 0; recoveryTimer++;
            if (recoveryTimer >= BrainConfig.ENV_RECOVERY_INTERVAL && sanity < 100) {
                BrainManager.addSanity(1); recoveryTimer = 0;
                AtomicParasites.LOGGER.info("[Brain] 📈 Sanity +1 (Safe) | Night: {} | Light: {} | New: {}",
                        night, blockLight, (int)BrainManager.getCurrentSanity());
            }
        }
        // === ⏸ НЕЙТРАЛЬНАЯ ЗОНА ===
        else { resetTimers(); }

        // === ДЕБАГ ===
        if (player.ticksExisted % 40 == 0) {
            AtomicParasites.LOGGER.info("=== [Brain] 🌍 ENV DEBUG ===");
            AtomicParasites.LOGGER.info("⏰ Time: {} ({}) | Night: {}",
                    player.world.getWorldTime(), player.world.getWorldTime() % 24000, night);
            AtomicParasites.LOGGER.info("🌌 CanSeeSky: {} | UnderSolidCover: {} | BlockLight: {}",
                    canSeeSky, underSolidCover, blockLight);
            AtomicParasites.LOGGER.info("🧠 Sanity: {} | Timers: dark={} | rec={}",
                    (int)sanity, darknessTimer, recoveryTimer);
        }
    }

    private static boolean isNightTime(World world) {
        long t = world.getWorldTime() % 24000;
        return t > 13000 && t < 23000;
    }

    private static boolean isUnderSolidCover(AbstractClientPlayer player) {
        BlockPos pos = new BlockPos(player.posX, player.posY + 1.6, player.posZ);
        for (int y = 0; y < 3; y++) {
            BlockPos check = pos.up(y);
            if (!player.world.isBlockLoaded(check)) continue;
            Block block = player.world.getBlockState(check).getBlock();
            if (block.isAir(player.world.getBlockState(check), player.world, check)) continue;
            if (BrainConfig.isBlockIgnoredForCover(block)) continue; // камень/земля — не укрытие!
            int opacity = player.world.getBlockState(check).getLightOpacity(player.world, check);
            if (opacity >= 10) return true;
            if (player.world.getBlockState(check).isOpaqueCube() && player.world.getBlockState(check).isFullCube()) return true;
        }
        return false;
    }

    private static void resetTimers() { darknessTimer = 0; recoveryTimer = 0; }
    public static void reset() { resetTimers(); }
    public static int getDarknessTimer() { return darknessTimer; }
    public static int getRecoveryTimer() { return recoveryTimer; }
}