package com.stefanparmezan.atomic_parasites.brain;

import com.stefanparmezan.atomic_parasites.main.AtomicParasites;
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

        int blockLight = player.world.getLightFor(EnumSkyBlock.BLOCK, pos);
        int skyLight = player.world.getLightFor(EnumSkyBlock.SKY, pos);

        // 🌟 ПОВЕРХНОСТЬ = есть ЛЮБОЙ свет неба (проникает сквозь деревья, листву!)
        boolean onSurface = skyLight > 0;

        float sanity = BrainManager.getCurrentSanity();

        // 🌑 ОПАСНО:
        // 1. Ночью на поверхности (skyLight > 0) — даже под деревьями!
        // 2. В темноте (blockLight <= 8) + НЕ на поверхности (пещера)
        boolean isDangerous = (night && onSurface)
                || (blockLight <= BrainConfig.DARKNESS_LIGHT_THRESHOLD && !onSurface);

        // ☀️ БЕЗОПАСНО:
        // 1. Яркий искусственный свет (факел ≥ 11) — всегда
        // 2. Днём + на поверхности (skyLight > 0) — включая деревья!
        boolean isSafe = blockLight >= BrainConfig.BRIGHT_LIGHT_THRESHOLD
                || (!night && onSurface && skyLight >= 10);

        if (isDangerous) {
            recoveryTimer = 0;
            darknessTimer++;
            if (darknessTimer >= BrainConfig.ENV_DECAY_INTERVAL && sanity > 0) {
                BrainManager.addSanity(-1);
                darknessTimer = 0;
                AtomicParasites.LOGGER.info("[Brain] 📉 Sanity -1 | Night:{} Surface:{} Sky:{} Block:{}",
                        night, onSurface, skyLight, blockLight);
            }
        }
        else if (isSafe) {
            darknessTimer = 0;
            recoveryTimer++;
            if (recoveryTimer >= BrainConfig.ENV_RECOVERY_INTERVAL && sanity < 100) {
                BrainManager.addSanity(1);
                recoveryTimer = 0;
                AtomicParasites.LOGGER.info("[Brain] 📈 Sanity +1 | Night:{} Surface:{} Sky:{} Block:{}",
                        night, onSurface, skyLight, blockLight);
            }
        }
        else {
            resetTimers(); // Нейтральная зона
        }

        // === ДЕБАГ каждые 2 секунды ===
        if (player.ticksExisted % 40 == 0) {
            AtomicParasites.LOGGER.info("=== [Brain] 🌍 ENV ===");
            AtomicParasites.LOGGER.info("⏰ Time: {} | Night: {} | Surface:{} Sky:{} Block:{}",
                    player.world.getWorldTime() % 24000, night, onSurface, skyLight, blockLight);
            AtomicParasites.LOGGER.info("🧠 Sanity: {} | Timers: dark={} rec={}",
                    (int)sanity, darknessTimer, recoveryTimer);
        }
    }

    private static boolean isNightTime(World world) {
        long t = world.getWorldTime() % 24000;
        return t > 13000 && t < 23000;
    }

    private static void resetTimers() {
        darknessTimer = 0;
        recoveryTimer = 0;
    }

    public static void reset() { resetTimers(); }
    public static int getDarknessTimer() { return darknessTimer; }
    public static int getRecoveryTimer() { return recoveryTimer; }
}