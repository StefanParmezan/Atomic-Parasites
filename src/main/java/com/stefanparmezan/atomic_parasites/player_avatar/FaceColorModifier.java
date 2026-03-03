package com.stefanparmezan.atomic_parasites.player_avatar;

import net.minecraft.client.entity.AbstractClientPlayer;

public class FaceColorModifier {

    // Горящее лицо — серый цвет
    private static final float BURN_BASE_R = 0.5f;
    private static final float BURN_BASE_G = 0.5f;
    private static final float BURN_BASE_B = 0.5f;
    private static final float BURN_DARK_R = 0.2f;
    private static final float BURN_DARK_G = 0.2f;
    private static final float BURN_DARK_B = 0.2f;

    private static final float NORMAL_R = 1.0f;
    private static final float NORMAL_G = 1.0f;
    private static final float NORMAL_B = 1.0f;

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

    public static ColorResult calculate(AbstractClientPlayer player) {
        boolean isBurning = player.isBurning();

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