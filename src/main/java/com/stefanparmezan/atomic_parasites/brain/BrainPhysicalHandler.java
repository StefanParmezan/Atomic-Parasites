package com.stefanparmezan.atomic_parasites.brain;

import com.stefanparmezan.atomic_parasites.main.AtomicParasites;
import net.minecraft.client.entity.AbstractClientPlayer;

public class BrainPhysicalHandler {

    private static float lastHealth = -1;
    private static float lastHealthPercent = -1;
    private static boolean wasDead = false;
    private static double lastMotionX = 0, lastMotionY = 0, lastMotionZ = 0;
    private static int explosionCooldown = 0;

    public static void tick(AbstractClientPlayer player) {
        if (player.capabilities.isCreativeMode) {
            lastHealth = player.getHealth();
            lastHealthPercent = (lastHealth / player.getMaxHealth()) * 100f;
            return;
        }

        checkDeathRespawn(player);
        checkHealthPenalty(player);
        checkDamageFlash(player);
        checkExplosionShake(player);
    }

    private static void checkDeathRespawn(AbstractClientPlayer player) {
        boolean dead = player.getHealth() <= 0;
        if (!wasDead && dead) wasDead = true;
        else if (wasDead && !dead && player.getHealth() > 0) {
            BrainManager.resetAfterDeath();
            AtomicParasites.LOGGER.info("[Brain] 🔄 Respawned | Sanity -> {}%", BrainConfig.SANITY_AFTER_DEATH);
            wasDead = false;
        }
    }

    private static void checkHealthPenalty(AbstractClientPlayer player) {
        float cur = player.getHealth(), max = player.getMaxHealth();
        float curPct = (cur / max) * 100f;
        if (lastHealthPercent > 0) {
            float loss = lastHealthPercent - curPct;
            if (loss >= BrainConfig.HP_LOSS_THRESHOLD) {
                int steps = (int)(loss / BrainConfig.HP_LOSS_THRESHOLD);
                int penalty = steps * BrainConfig.SANITY_PENALTY;
                int actual = Math.min(penalty, (int)BrainManager.getCurrentSanity());
                BrainManager.addSanity(-actual);
                AtomicParasites.LOGGER.info("[Brain] 💔 HP Penalty | Lost: {:.1f}% | Sanity: -{} | New: {}",
                        loss, actual, (int)BrainManager.getCurrentSanity());
            }
        }
        lastHealthPercent = curPct; lastHealth = cur;
    }

    private static void checkDamageFlash(AbstractClientPlayer player) {
        float cur = player.getHealth();
        if (lastHealth > 0 && cur < lastHealth - 0.1f) {
            AtomicParasites.LOGGER.info("[Brain] 💥 Damage | Flash");
            BrainManager.triggerFlash();
        }
    }

    private static void checkExplosionShake(AbstractClientPlayer player) {
        if (explosionCooldown <= 0) {
            double dx = Math.abs(player.motionX - lastMotionX);
            double dy = Math.abs(player.motionY - lastMotionY);
            double dz = Math.abs(player.motionZ - lastMotionZ);
            if (dx + dy + dz > BrainConfig.MOTION_THRESHOLD && !player.onGround) {
                AtomicParasites.LOGGER.info("[Brain] 🌪 Knockback | Shake");
                BrainManager.triggerShake();
                explosionCooldown = BrainConfig.EXPLOSION_COOLDOWN;
            }
        } else explosionCooldown--;
        lastMotionX = player.motionX; lastMotionY = player.motionY; lastMotionZ = player.motionZ;
    }

    public static void reset() {
        lastHealth = -1; lastHealthPercent = -1; wasDead = false;
        lastMotionX = lastMotionY = lastMotionZ = 0; explosionCooldown = 0;
    }
}