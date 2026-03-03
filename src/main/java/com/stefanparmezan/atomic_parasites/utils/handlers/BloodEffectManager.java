package com.stefanparmezan.atomic_parasites.utils.handlers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.init.MobEffects;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class BloodEffectManager {

    // Сделал public, чтобы FaceOverlayHandler мог использовать
    public static final Random RAND = new Random();

    private static final int MAX_BLOOD_SPOTS = 12;
    private static final float SPOT_MIN_SIZE = 3.0f;
    private static final float SPOT_MAX_SIZE = 8.0f;
    private static final float HP_DAMAGE_THRESHOLD = 0.5f;

    private static float lastHealth = -1;
    private static float maxHealth = -1;
    private static final List<BloodSpot> activeSpots = new ArrayList<>();

    public static class BloodSpot {
        public final float x;
        public final float y;
        public final float size;
        public final float[] offsetsX;
        public final float[] offsetsY;
        public final int points;

        public BloodSpot(float x, float y, float size) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.points = 5 + RAND.nextInt(4);
            this.offsetsX = new float[this.points];
            this.offsetsY = new float[this.points];
            for (int i = 0; i < this.points; i++) {
                this.offsetsX[i] = (RAND.nextFloat() - 0.5f) * 0.4f;
                this.offsetsY[i] = (RAND.nextFloat() - 0.5f) * 0.4f;
            }
        }
    }

    public static void update(AbstractClientPlayer player) {
        float currentHealth = player.getHealth();
        float currentMaxHealth = player.getMaxHealth();

        if (lastHealth < 0) {
            lastHealth = currentHealth;
            maxHealth = currentMaxHealth;
            return;
        }

        float damage = lastHealth - currentHealth;

        // Кровь только при физическом уроне
        if (damage >= HP_DAMAGE_THRESHOLD && isPhysicalDamage(player)) {
            spawnBloodSpot();
        }

        // Удаление крови при отхиле
        if (currentHealth > lastHealth) {
            float healed = currentHealth - lastHealth;
            int spotsToRemove = (int) (healed / currentMaxHealth * MAX_BLOOD_SPOTS);
            for (int i = 0; i < spotsToRemove && !activeSpots.isEmpty(); i++) {
                activeSpots.remove(0);
            }
        }

        if (currentHealth >= currentMaxHealth * 0.95f) {
            activeSpots.clear();
        }

        lastHealth = currentHealth;
        maxHealth = currentMaxHealth;
    }

    /**
     * Проверка: был ли урон физическим.
     * Если на игроке есть активные эффекты урона (яд, иссушение) или он горит,
     * считаем урон магическим/экологическим — крови не будет.
     */
    private static boolean isPhysicalDamage(AbstractClientPlayer player) {
        if (player.isBurning()) return false;
        if (player.getActivePotionEffect(MobEffects.POISON) != null) return false;
        if (player.getActivePotionEffect(MobEffects.WITHER) != null) return false;
        if (player.getActivePotionEffect(MobEffects.HUNGER) != null) return false;
        return true;
    }

    private static void spawnBloodSpot() {
        if (activeSpots.size() >= MAX_BLOOD_SPOTS) {
            activeSpots.remove(0);
        }
        float x = 0.1f + RAND.nextFloat() * 0.8f;
        float y = 0.1f + RAND.nextFloat() * 0.8f;
        float size = SPOT_MIN_SIZE + RAND.nextFloat() * (SPOT_MAX_SIZE - SPOT_MIN_SIZE);
        activeSpots.add(new BloodSpot(x, y, size));
    }

    public static List<BloodSpot> getActiveSpots() {
        return activeSpots;
    }

    public static void reset() {
        lastHealth = -1;
        maxHealth = -1;
        activeSpots.clear();
    }
}