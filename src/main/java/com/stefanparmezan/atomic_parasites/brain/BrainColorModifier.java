package com.stefanparmezan.atomic_parasites.brain;

import com.stefanparmezan.atomic_parasites.brain.effects.PotionFearOfDeath;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.potion.PotionEffect;

public class BrainColorModifier {

    // === 😰 СТРАХ СМЕРТИ — фиолетовый цвет #e967cf ===
    // Hex #e967cf → RGB: R=233, G=103, B=207 → Float: /255
    private static final float FEAR_BASE_R = 233f / 255f;  // ≈ 0.914
    private static final float FEAR_BASE_G = 103f / 255f;  // ≈ 0.404
    private static final float FEAR_BASE_B = 207f / 255f;  // ≈ 0.812

    // Тень для объёма (70% от базового)
    private static final float FEAR_DARK_R = FEAR_BASE_R * 0.7f;  // ≈ 0.640
    private static final float FEAR_DARK_G = FEAR_BASE_G * 0.7f;  // ≈ 0.283
    private static final float FEAR_DARK_B = FEAR_BASE_B * 0.7f;  // ≈ 0.568

    // === ⚪ Нормальный цвет (белый) ===
    private static final float NORMAL_R = 1.0f;
    private static final float NORMAL_G = 1.0f;
    private static final float NORMAL_B = 1.0f;
    private static final float NORMAL_DARK_R = NORMAL_R * 0.7f;
    private static final float NORMAL_DARK_G = NORMAL_G * 0.7f;
    private static final float NORMAL_DARK_B = NORMAL_B * 0.7f;

    /**
     * Результат расчёта цвета мозга
     */
    public static class BrainColorResult {
        public final float baseR, baseG, baseB;
        public final float darkR, darkG, darkB;
        public final boolean hasEffect;

        public BrainColorResult(float baseR, float baseG, float baseB,
                                float darkR, float darkG, float darkB,
                                boolean hasEffect) {
            this.baseR = baseR;
            this.baseG = baseG;
            this.baseB = baseB;
            this.darkR = darkR;
            this.darkG = darkG;
            this.darkB = darkB;
            this.hasEffect = hasEffect;
        }

        /** Нормальный (белый) цвет */
        public static BrainColorResult normal() {
            return new BrainColorResult(
                    NORMAL_R, NORMAL_G, NORMAL_B,
                    NORMAL_DARK_R, NORMAL_DARK_G, NORMAL_DARK_B,
                    false
            );
        }

        /** Фиолетовый цвет страха */
        public static BrainColorResult fear() {
            return new BrainColorResult(
                    FEAR_BASE_R, FEAR_BASE_G, FEAR_BASE_B,
                    FEAR_DARK_R, FEAR_DARK_G, FEAR_DARK_B,
                    true
            );
        }
    }

    /**
     * Рассчитывает цвет мозга в зависимости от эффектов игрока
     * @param player игрок
     * @return результат с цветами для отрисовки
     */
    public static BrainColorResult calculate(AbstractClientPlayer player) {
        // Проверяем, есть ли эффект страха смерти
        boolean hasFearEffect = player.isPotionActive(PotionFearOfDeath.INSTANCE);

        if (hasFearEffect) {
            return BrainColorResult.fear();
        }

        return BrainColorResult.normal();
    }

    /**
     * Быстрая проверка: нужно ли применять фиолетовый цвет?
     */
    public static boolean isFearActive(AbstractClientPlayer player) {
        return player.isPotionActive(PotionFearOfDeath.INSTANCE);
    }
}