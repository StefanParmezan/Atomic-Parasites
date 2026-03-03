package com.stefanparmezan.atomic_parasites.utils.handlers;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

public class FaceColorModifier {

    // === НАСТРОЙКИ ЦВЕТОВ ===

    // Горящее лицо: темно-серый с вариациями
    private static final float BURN_BASE_R = 0.5f;
    private static final float BURN_BASE_G = 0.5f;
    private static final float BURN_BASE_B = 0.5f;
    private static final float BURN_DARK_R = 0.2f;
    private static final float BURN_DARK_G = 0.2f;
    private static final float BURN_DARK_B = 0.2f;

    // Отравленное лицо: зеленое с вариациями
    private static final float POISON_BASE_R = 0.3f;
    private static final float POISON_BASE_G = 0.7f;
    private static final float POISON_BASE_B = 0.3f;
    private static final float POISON_DARK_R = 0.1f;
    private static final float POISON_DARK_G = 0.4f;
    private static final float POISON_DARK_B = 0.1f;

    // Иссушение (Wither): тёмное лицо с фиолетовыми пятнами
    private static final float WITHER_BASE_R = 0.25f;
    private static final float WITHER_BASE_G = 0.20f;
    private static final float WITHER_BASE_B = 0.30f;
    private static final float WITHER_DARK_R = 0.45f;
    private static final float WITHER_DARK_G = 0.15f;
    private static final float WITHER_DARK_B = 0.55f;

    // Нормальное лицо
    private static final float NORMAL_R = 1.0f;
    private static final float NORMAL_G = 1.0f;
    private static final float NORMAL_B = 1.0f;

    /**
     * Результат применения модификатора
     */
    public static class ColorResult {
        public final float baseR, baseG, baseB;
        public final float darkR, darkG, darkB;
        public final boolean hasEffect;

        public ColorResult(float baseR, float baseG, float baseB,
                           float darkR, float darkG, float darkB,
                           boolean hasEffect) {
            this.baseR = baseR; this.baseG = baseG; this.baseB = baseB;
            this.darkR = darkR; this.darkG = darkG; this.darkB = darkB;
            this.hasEffect = hasEffect;
        }

        public static ColorResult normal() {
            return new ColorResult(NORMAL_R, NORMAL_G, NORMAL_B,
                    NORMAL_R * 0.7f, NORMAL_G * 0.7f, NORMAL_B * 0.7f,
                    false);
        }
    }

    /**
     * Рассчитывает цвета для отрисовки лица
     */
    public static ColorResult calculate(AbstractClientPlayer player) {
        boolean isBurning = player.isBurning();
        PotionEffect poison = player.getActivePotionEffect(MobEffects.POISON);
        PotionEffect wither = player.getActivePotionEffect(MobEffects.WITHER);
        boolean isPoisoned = poison != null;
        boolean isWithered = wither != null;

        // Приоритет: иссушение > отравление > горение > норма
        if (isWithered) {
            float intensity = Math.min(1.0f, wither.getAmplifier() * 0.15f + 0.6f);
            return new ColorResult(
                    WITHER_BASE_R * intensity, WITHER_BASE_G * intensity, WITHER_BASE_B * intensity,
                    WITHER_DARK_R * intensity, WITHER_DARK_G * intensity, WITHER_DARK_B * intensity,
                    true
            );
        }

        if (isPoisoned) {
            float intensity = Math.min(1.0f, poison.getAmplifier() * 0.2f + 0.5f);
            return new ColorResult(
                    POISON_BASE_R * intensity, POISON_BASE_G * intensity, POISON_BASE_B * intensity,
                    POISON_DARK_R * intensity, POISON_DARK_G * intensity, POISON_DARK_B * intensity,
                    true
            );
        }

        if (isBurning) {
            return new ColorResult(
                    BURN_BASE_R, BURN_BASE_G, BURN_BASE_B,
                    BURN_DARK_R, BURN_DARK_G, BURN_DARK_B,
                    true
            );
        }

        return ColorResult.normal();
    }
}