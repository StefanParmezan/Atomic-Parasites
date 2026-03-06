package com.stefanparmezan.atomic_parasites.brain;

import net.minecraft.init.Blocks;
import net.minecraft.block.Block;

public class BrainConfig {

    // === 🌑 Окружение ===
    public static final int DARKNESS_LIGHT_THRESHOLD = 8;
    public static final int BRIGHT_LIGHT_THRESHOLD = 11;
    public static final int ENV_DECAY_INTERVAL = 35;
    public static final int ENV_RECOVERY_INTERVAL = 130;

    // === 💔 Физика ===
    public static final int HP_LOSS_THRESHOLD = 4;
    public static final int SANITY_PENALTY = 1;
    public static final double MOTION_THRESHOLD = 0.8;
    public static final int EXPLOSION_COOLDOWN = 60;

    // === 💀 Смерть ===
    public static final int SANITY_AFTER_DEATH = 30;
    public static final String[] DEATH_MESSAGES = {
            "%player% решил подчиниться голосам в голове",
            "%player% потерял рассудок окончательно",
            "%player% растворился в безумии",
            "Рассудок %player% не выдержал этого мира",
            "%player% решил поспорить с голосом в голове"
    };


    public static String getRandomDeathMessage(String playerName) {
        String msg = DEATH_MESSAGES[(int)(Math.random() * DEATH_MESSAGES.length)];
        return msg.replace("%player%", playerName);
    }
}