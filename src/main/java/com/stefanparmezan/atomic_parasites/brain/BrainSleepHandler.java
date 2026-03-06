package com.stefanparmezan.atomic_parasites.brain;

import com.stefanparmezan.atomic_parasites.main.AtomicParasites;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BrainSleepHandler {

    private static final float SLEEP_RECOVERY_PERCENT = 0.30f;

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        World world = player.getEntityWorld();

        // 1. Логика только на СЕРВЕРЕ
        if (world.isRemote) return;
        if (player.capabilities.isCreativeMode) return;

        // 2. ПРОВЕРКА: Был ли пропуск ночи?
        // При скипе время ставится в 0 (или 24000, 48000...).
        // Остаток от деления на 24000 будет близок к 0.
        // Допуск 120 тиков (6 секунд) на случай задержек.
        long timeOfDay = world.getWorldTime() % 24000;
        boolean nightSkipped = (timeOfDay < 120);

        if (nightSkipped) {
            applySleepRecovery();
            AtomicParasites.LOGGER.info("[Brain] 😴 Sleep Recovery Applied for {} | Time: {}",
                    player.getName(), world.getWorldTime());
        } else {
            // Игрок просто вышел из кровати (Esc) посреди ночи
            AtomicParasites.LOGGER.info("[Brain] 😴 Sleep Interrupted for {} | No Recovery",
                    player.getName());
        }
    }

    private static void applySleepRecovery() {
        float currentSanity = BrainManager.getCurrentSanity();
        float recoveryAmount = 100.0f * SLEEP_RECOVERY_PERCENT;
        float newSanity = Math.min(100.0f, currentSanity + recoveryAmount);

        BrainManager.setSanity(newSanity);

        AtomicParasites.LOGGER.info("[Brain] 😴 Recovery | Old: {} | +{} | New: {}",
                (int)currentSanity, (int)recoveryAmount, (int)newSanity);
    }

    public static void reset() {}
}