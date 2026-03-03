package com.stefanparmezan.atomic_parasites.player_avatar;

import net.minecraft.client.entity.AbstractClientPlayer;

public class SkullEffectManager {

    private static final int SKULL_FADE_IN_TIME = 20;   // 1 секунда (появление)
    private static final int SKULL_FADE_OUT_TIME = 100; // 5 секунд (исчезновение)
    private static final double SKULL_HP_THRESHOLD = 3.0; // 2 сердца

    private static boolean hasSkull = false;
    private static boolean isFadingOut = false;
    private static int skullAge = 0;
    private static float skullOpacity = 0.0f;

    public static void update(AbstractClientPlayer player) {
        double health = player.getHealth();

        // Игрок получил урон до порога — череп появляется
        if (health <= SKULL_HP_THRESHOLD && !hasSkull) {
            hasSkull = true;
            isFadingOut = false;
            skullAge = 0;
            skullOpacity = 0.0f;
        }
        // Игрок отхилился выше порога — череп начинает исчезать
        else if (health > SKULL_HP_THRESHOLD && hasSkull && !isFadingOut) {
            isFadingOut = true;
            skullAge = 0;
        }

        // Обновляем прозрачность
        if (hasSkull) {
            if (!isFadingOut) {
                // Появление (fade in)
                skullAge++;
                skullOpacity = Math.min(1.0f, skullAge / (float) SKULL_FADE_IN_TIME);
            } else {
                // Исчезновение (fade out)
                skullAge++;
                skullOpacity = Math.max(0.0f, 1.0f - (skullAge / (float) SKULL_FADE_OUT_TIME));

                // Череп полностью исчез
                if (skullOpacity <= 0.0f) {
                    hasSkull = false;
                    isFadingOut = false;
                    skullAge = 0;
                    skullOpacity = 0.0f;
                }
            }
        }
    }

    public static boolean shouldRenderSkull() {
        return hasSkull && skullOpacity > 0.01f;
    }

    public static float getSkullOpacity() {
        return skullOpacity;
    }

    public static void reset() {
        hasSkull = false;
        isFadingOut = false;
        skullAge = 0;
        skullOpacity = 0.0f;
    }
}