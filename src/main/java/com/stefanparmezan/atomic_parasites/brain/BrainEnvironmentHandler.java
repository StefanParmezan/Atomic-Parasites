package com.stefanparmezan.atomic_parasites.brain;

import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
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
        // ✅ ИСПРАВЛЕНО: используем BrainConfig вместо BrainLightItemHandler.MIN_LIGHT_LEVEL
        boolean hasBrightItem = itemLightLevel >= BrainConfig.ITEM_LIGHT_MIN_LEVEL;

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
            // ... (логи без изменений)
        }

        if (isDangerous) {
            recoveryTimer = 0;

            if (hasBrightItem) {
                // === 🕯️ ЕСТЬ СВЕТЯЩИЙСЯ ПРЕДМЕТ - РАСЧЁТ МНОЖИТЕЛЯ ===
                int slowdownMultiplier;
                // ✅ ИСПРАВЛЕНО: используем BrainConfig
                if (itemLightLevel >= BrainConfig.ITEM_LIGHT_HIGH_THRESHOLD) {
                    slowdownMultiplier = BrainConfig.ITEM_LIGHT_MULTIPLIER_HIGH;
                } else {
                    slowdownMultiplier = BrainConfig.ITEM_LIGHT_MULTIPLIER_LOW;
                }

                int effectiveThreshold = BrainConfig.ENV_DECAY_INTERVAL * slowdownMultiplier;
                darknessTimer++;

                if (darknessTimer >= effectiveThreshold && sanity > 0) {
                    BrainManager.addSanity(-1);
                    darknessTimer = 0;
                }
            }
            // === 🌑 НЕТ ПРЕДМЕТА - ОБЫЧНОЕ ПАДЕНИЕ ===
            else {
                darknessTimer++;
                if (darknessTimer >= BrainConfig.ENV_DECAY_INTERVAL && sanity > 0) {
                    BrainManager.addSanity(-1);
                    darknessTimer = 0;
                }
            }
        }
        // В блоке isSafe (восстановление):
        else if (isSafe) {
            darknessTimer = 0;
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