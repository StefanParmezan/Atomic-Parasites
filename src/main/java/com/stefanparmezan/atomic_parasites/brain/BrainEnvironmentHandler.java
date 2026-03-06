package com.stefanparmezan.atomic_parasites.brain;

import com.stefanparmezan.atomic_parasites.main.AtomicParasites;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class BrainEnvironmentHandler {

    private static int darknessTimer = 0;
    private static int recoveryTimer = 0;
    private static int lastLoggedSecond = -1;

    public static void tick(AbstractClientPlayer player) {
        if (player.capabilities.isCreativeMode) { resetTimers(); return; }

        boolean night = isNightTime(player.world);
        BlockPos pos = new BlockPos(player.posX, player.posY + 1.6, player.posZ);

        int blockLight = player.world.getLightFor(EnumSkyBlock.BLOCK, pos);
        int skyLight = player.world.getLightFor(EnumSkyBlock.SKY, pos);
        boolean onSurface = skyLight > 0;

        // 🕯️ ПРОВЕРКА ПРЕДМЕТА
        int itemLightLevel = BrainLightItemHandler.getLightLevel(player);
        boolean hasBrightItem = itemLightLevel >= BrainLightItemHandler.MIN_LIGHT_LEVEL;

        float sanity = BrainManager.getCurrentSanity();

        // 🌑 ОПАСНО: Ночь на поверхности ИЛИ темнота в пещере
        boolean isDangerous = (night && onSurface)
                || (blockLight <= BrainConfig.DARKNESS_LIGHT_THRESHOLD && !onSurface);

        // ☀️ БЕЗОПАСНО: Яркий свет ИЛИ день на поверхности
        boolean isSafe = blockLight >= BrainConfig.BRIGHT_LIGHT_THRESHOLD
                || (!night && onSurface && skyLight >= 10);

        // 📝 ЛОГ: каждое изменение секунды
        int currentSecond = (int)(player.world.getWorldTime() / 20) % 60;
        if (currentSecond != lastLoggedSecond) {
            lastLoggedSecond = currentSecond;
            AtomicParasites.LOGGER.info("=== [Brain] 🌍 ENV TICK ===");
            AtomicParasites.LOGGER.info("⏰ Time: {} | Night: {} | Surface: {} | SkyLight: {} | BlockLight: {}",
                    player.world.getWorldTime() % 24000, night, onSurface, skyLight, blockLight);
            AtomicParasites.LOGGER.info("🕯️ ItemLight: {} | HasBrightItem: {} | Sanity: {}",
                    itemLightLevel, hasBrightItem, (int)sanity);
            AtomicParasites.LOGGER.info("⚠️ isDangerous: {} | isSafe: {}", isDangerous, isSafe);
        }

        if (isDangerous) {
            recoveryTimer = 0;

            if (hasBrightItem) {
                // === 🕯️ ЕСТЬ СВЕТЯЩИЙСЯ ПРЕДМЕТ - РАСЧЁТ МНОЖИТЕЛЯ ===
                int slowdownMultiplier;
                if (itemLightLevel >= 13) {
                    slowdownMultiplier = 3;  // x3 для света 13-15
                } else {
                    slowdownMultiplier = 2;  // x2 для света 10-12
                }

                // Умножаем порог на множитель
                int effectiveThreshold = BrainConfig.ENV_DECAY_INTERVAL * slowdownMultiplier;

                darknessTimer++;

                if (darknessTimer >= effectiveThreshold && sanity > 0) {
                    BrainManager.addSanity(-1);
                    darknessTimer = 0;
                    AtomicParasites.LOGGER.info("[Brain] 📉 Sanity -1 (SLOW x{}) | Item: {} | New: {}",
                            slowdownMultiplier, BrainLightItemHandler.getHeldLightItemName(player),
                            (int)BrainManager.getCurrentSanity());
                } else {
                    // Лог для отладки - каждые 20 тиков (1 секунда)
                    if (player.ticksExisted % 20 == 0) {
                        AtomicParasites.LOGGER.info("[Brain] ⏳ SLOW Progress: {} / {} ticks (x{})",
                                darknessTimer, effectiveThreshold, slowdownMultiplier);
                    }
                }
            }
            // === 🌑 НЕТ ПРЕДМЕТА - ОБЫЧНОЕ ПАДЕНИЕ ===
            else {
                darknessTimer++;
                if (darknessTimer >= BrainConfig.ENV_DECAY_INTERVAL && sanity > 0) {
                    BrainManager.addSanity(-1);
                    darknessTimer = 0;
                    AtomicParasites.LOGGER.info("[Brain] 📉 Sanity -1 (FAST) | New: {}",
                            (int)BrainManager.getCurrentSanity());
                }
            }
        }
        // В блоке isSafe (восстановление):
        else if (isSafe) {
            darknessTimer = 0;

            // 🔄 МНОЖИТЕЛЬ ТОЛЬКО ОТ ЦВЕТОВ (сон обрабатывается отдельно!)
            float recoveryMultiplier = BrainRecoveryHandler.getRecoveryMultiplier(player);
            int effectiveRecoveryThreshold = (int)(BrainConfig.ENV_RECOVERY_INTERVAL / recoveryMultiplier);

            recoveryTimer++;
            if (recoveryTimer >= effectiveRecoveryThreshold && sanity < 100) {
                BrainManager.addSanity(1);
                recoveryTimer = 0;
            }
        }
        else {
            resetTimers();
            AtomicParasites.LOGGER.info("[Brain] ⏸ Neutral zone | Timers reset");
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