package com.stefanparmezan.atomic_parasites.brain;

import com.stefanparmezan.atomic_parasites.main.AtomicParasites;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class BrainEventHandler {

    // === НАСТРОЙКИ БАЛАНСА ===
    private static final int DECAY_INTERVAL = 120;
    private static final int RECOVERY_INTERVAL = 360;

    // === Внутренние таймеры ===
    private static int darknessTimer = 0;
    private static int recoveryTimer = 0;

    // === Для детекта урона/взрыва ===
    private static float lastHealth = -1;
    private static double lastMotionX = 0, lastMotionY = 0, lastMotionZ = 0;
    private static int explosionCooldown = 0;
    private static final double MOTION_THRESHOLD = 0.8;
    private static final int EXPLOSION_COOLDOWN = 60;

    public static void tick(AbstractClientPlayer player) {
        updateDamageDetection(player);
        updateSanity(player);
        BrainManager.updateEffects();
    }

    /**
     * === ПРОВЕРКА НОЧИ ===
     */
    private static boolean isNightTime(World world) {
        long time = world.getWorldTime() % 24000;
        return time > 13000 && time < 23000;
    }

    /**
     * === ПРОВЕРКА: НАХОДИТСЯ ЛИ ИГРОК ПОД НАДЁЖНЫМ УКРЫТИЕМ ===
     * Возвращает true, только если над головой есть НЕПРОЗРАЧНЫЙ блок
     * Листья, стекло, плиты — НЕ считаются укрытием!
     */
    private static boolean isUnderSolidCover(AbstractClientPlayer player) {
        BlockPos pos = new BlockPos(player.posX, player.posY + 1.6, player.posZ);

        // Проверяем несколько блоков вверх
        for (int y = 0; y < 3; y++) {
            BlockPos checkPos = pos.up(y);

            if (!player.world.isBlockLoaded(checkPos)) {
                continue;
            }

            // Получаем состояние блока
            net.minecraft.block.state.IBlockState state = player.world.getBlockState(checkPos);

            // Если блок воздух — идём выше
            if (state.getBlock().isAir(state, player.world, checkPos)) {
                continue;
            }

            // ✅ ГЛАВНАЯ ПРОВЕРКА: светонепроницаемость
            // getLightOpacity() возвращает насколько блок блокирует свет (0-15)
            // Листья = 2-3, Стекло = 0, Полные блоки = 15
            int lightOpacity = state.getLightOpacity(player.world, checkPos);

            // Если блок достаточно непрозрачный (>= 10) — считаем это укрытием
            if (lightOpacity >= 10) {
                return true;
            }

            // ✅ ИСПРАВЛЕНО для 1.12.2: проверяем полный куб через isOpaqueCube()
            if (state.isOpaqueCube() && state.isFullCube()) {
                return true;
            }
        }

        // Над головой нет надёжных блоков
        return false;
    }

    /**
     * === Логика изменения рассудка ===
     */
    private static void updateSanity(AbstractClientPlayer player) {
        boolean night = isNightTime(player.world);
        boolean underCover = isUnderSolidCover(player);
        float sanity = BrainManager.getCurrentSanity();

        // Получаем блочный свет (факелы, лава и т.д.)
        BlockPos pos = new BlockPos(player.posX, player.posY + 1.6, player.posZ);
        int blockLight = player.world.getLightFor(EnumSkyBlock.BLOCK, pos);

        // === ЛОГИКА: ТЬМА ===
        // Условия: Ночь + НЕТ надёжного укрытия + Нет яркого источника света
        if (night && !underCover && blockLight <= 7) {
            recoveryTimer = 0;
            darknessTimer++;

            if (darknessTimer >= DECAY_INTERVAL && sanity > 0) {
                BrainManager.addSanity(-1);
                darknessTimer = 0;
                AtomicParasites.LOGGER.info("[Brain] 📉 Sanity -1 (Night+Exposed) | BlockLight: {} | New: {}",
                        blockLight, (int)BrainManager.getCurrentSanity());
            }
        }
        // === ЛОГИКА: СВЕТ ===
        // Условия: Либо день, либо есть укрытие, либо яркий источник света
        else if (!night || underCover || blockLight >= 11) {
            darknessTimer = 0;
            recoveryTimer++;

            if (recoveryTimer >= RECOVERY_INTERVAL && sanity < 100) {
                BrainManager.addSanity(1);
                recoveryTimer = 0;
                AtomicParasites.LOGGER.info("[Brain] 📈 Sanity +1 (Safe) | Night: {} | Covered: {} | BlockLight: {} | New: {}",
                        night, underCover, blockLight, (int)BrainManager.getCurrentSanity());
            }
        }
        // === НЕЙТРАЛЬНАЯ ЗОНА ===
        else {
            darknessTimer = 0;
            recoveryTimer = 0;
        }

        // === ДЕБАГ ЛОГИ (каждые 2 секунды) ===
        if (player.ticksExisted % 40 == 0) {
            AtomicParasites.LOGGER.info("=== [Brain] 🧠 DEBUG TICK ===");
            AtomicParasites.LOGGER.info("⏰ Time: {} ({}) | Night: {}",
                    player.world.getWorldTime(), player.world.getWorldTime() % 24000, night);
            AtomicParasites.LOGGER.info("🏠 UnderCover: {} | BlockLight: {} | SkyLight: {}",
                    underCover, blockLight, player.world.getLightFor(EnumSkyBlock.SKY, pos));
            AtomicParasites.LOGGER.info("🧠 Sanity: {} | Timers: dark={} | rec={}",
                    (int)sanity, darknessTimer, recoveryTimer);
            AtomicParasites.LOGGER.info("================================");
        }
    }

    /**
     * === Детект урона и взрывов ===
     */
    private static void updateDamageDetection(AbstractClientPlayer player) {
        float currentHealth = player.getHealth();

        if (lastHealth > 0 && currentHealth < lastHealth - 0.1f) {
            AtomicParasites.LOGGER.info("[Brain] 💥 Damage detected | Triggering flash");
            BrainManager.triggerFlash();
        }
        lastHealth = currentHealth;

        if (explosionCooldown <= 0) {
            double dx = Math.abs(player.motionX - lastMotionX);
            double dy = Math.abs(player.motionY - lastMotionY);
            double dz = Math.abs(player.motionZ - lastMotionZ);
            double motionChange = dx + dy + dz;

            if (motionChange > MOTION_THRESHOLD && !player.onGround) {
                AtomicParasites.LOGGER.info("[Brain] 🌪 Knockback detected | Triggering shake");
                BrainManager.triggerShake();
                explosionCooldown = EXPLOSION_COOLDOWN;
            }
        } else {
            explosionCooldown--;
        }

        lastMotionX = player.motionX;
        lastMotionY = player.motionY;
        lastMotionZ = player.motionZ;
    }

    public static void reset() {
        darknessTimer = 0;
        recoveryTimer = 0;
        lastHealth = -1;
        lastMotionX = lastMotionY = lastMotionZ = 0;
        explosionCooldown = 0;
        BrainManager.reset();
    }
}