package com.stefanparmezan.atomic_parasites.brain;

import com.stefanparmezan.atomic_parasites.main.AtomicParasites;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BrainSleepHandler {

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        World world = player.getEntityWorld();
        if (world.isRemote) return;
        if (player.capabilities.isCreativeMode) return;

        long timeOfDay = world.getWorldTime() % 24000;
        boolean nightSkipped = (timeOfDay < BrainConfig.NIGHT_SKIP_THRESHOLD);

        if (nightSkipped) {
            applySleepRecovery();
            AtomicParasites.LOGGER.info("[Brain] 😴 Sleep Recovery Applied for {} | Time: {}",
                    player.getName(), world.getWorldTime());
        } else {
            AtomicParasites.LOGGER.info("[Brain] 😴 Sleep Interrupted for {} | No Recovery", player.getName());
        }
    }

    private static void applySleepRecovery() {
        float currentSanity = BrainManager.getCurrentSanity();
        float recoveryAmount = 100.0f * BrainConfig.SLEEP_RECOVERY_PERCENT;
        float newSanity = Math.min(100.0f, currentSanity + recoveryAmount);
        BrainManager.setSanity(newSanity);
        AtomicParasites.LOGGER.info("[Brain] 😴 Recovery | Old: {} | +{} | New: {}",
                (int)currentSanity, (int)recoveryAmount, (int)newSanity);
    }

    public static void reset() {}
}