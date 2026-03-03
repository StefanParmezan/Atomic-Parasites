package com.stefanparmezan.atomic_parasites.utils.handlers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.init.MobEffects;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class BloodEffectManager {

    public static final Random RAND = new Random();

    private static final int MAX_SPOTS = 10;
    private static final int SPOT_LIFETIME = 400;

    private static float lastHealth = -1;
    private static final List<EffectSpot> activeSpots = new ArrayList<>();

    public enum SpotType {
        BLOOD,
        BURN
    }

    public static class EffectSpot {
        public final int gridX;
        public final int gridY;
        public final SpotType type;
        public int age;

        public EffectSpot(int gridX, int gridY, SpotType type) {
            this.gridX = gridX;
            this.gridY = gridY;
            this.type = type;
            this.age = 0;
        }

        public void update() {
            age++;
        }

        public boolean isDead() {
            return age >= SPOT_LIFETIME;
        }

        public float getOpacity() {
            if (age < SPOT_LIFETIME * 0.7f) return 1.0f;
            return 1.0f - ((age - SPOT_LIFETIME * 0.7f) / (SPOT_LIFETIME * 0.3f));
        }
    }

    public static void update(AbstractClientPlayer player) {
        float currentHealth = player.getHealth();

        if (lastHealth < 0) {
            lastHealth = currentHealth;
            return;
        }

        float damage = lastHealth - currentHealth;
        if (damage >= 0.5f && isPhysicalDamage(player)) {
            // ИСПРАВЛЕНО: 2-5 пикселей
            int pixelCount = 2 + RAND.nextInt(4);
            for (int i = 0; i < pixelCount; i++) {
                spawnEffectSpot(SpotType.BLOOD);
            }
        }

        if (player.isBurning() && RAND.nextFloat() < 0.03f) {
            // ИСПРАВЛЕНО: 1-3 пикселя
            int pixelCount = 1 + RAND.nextInt(Math.max(1, 3));
            for (int i = 0; i < pixelCount; i++) {
                spawnEffectSpot(SpotType.BURN);
            }
        }

        Iterator<EffectSpot> it = activeSpots.iterator();
        while (it.hasNext()) {
            EffectSpot spot = it.next();
            spot.update();
            if (spot.isDead()) {
                it.remove();
            }
        }

        lastHealth = currentHealth;
    }

    private static boolean isPhysicalDamage(AbstractClientPlayer player) {
        if (player.isBurning()) return false;
        if (player.getActivePotionEffect(MobEffects.POISON) != null) return false;
        if (player.getActivePotionEffect(MobEffects.WITHER) != null) return false;
        if (player.getActivePotionEffect(MobEffects.HUNGER) != null) return false;
        return true;
    }

    private static void spawnEffectSpot(SpotType type) {
        if (activeSpots.size() >= MAX_SPOTS) {
            activeSpots.remove(0);
        }
        // ИСПРАВЛЕНО: всегда положительное число (8)
        int gridX = RAND.nextInt(8);
        int gridY = RAND.nextInt(8);
        activeSpots.add(new EffectSpot(gridX, gridY, type));
    }

    public static List<EffectSpot> getActiveSpots() {
        return activeSpots;
    }

    public static void reset() {
        lastHealth = -1;
        activeSpots.clear();
    }
}