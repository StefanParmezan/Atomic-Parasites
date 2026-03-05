package com.stefanparmezan.atomic_parasites.brain;

import net.minecraft.client.entity.AbstractClientPlayer;

public class BrainEventHandler {

    public static void tick(AbstractClientPlayer player) {
        BrainPhysicalHandler.tick(player);
        BrainEnvironmentHandler.tick(player);
        BrainManager.updateEffects();
    }

    public static void reset() {
        BrainEnvironmentHandler.reset();
        BrainPhysicalHandler.reset();
        BrainManager.reset();
    }

    public static int getDarknessTimer() { return BrainEnvironmentHandler.getDarknessTimer(); }
    public static int getRecoveryTimer() { return BrainEnvironmentHandler.getRecoveryTimer(); }
}