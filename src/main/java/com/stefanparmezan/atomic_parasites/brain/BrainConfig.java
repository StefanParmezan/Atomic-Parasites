package com.stefanparmezan.atomic_parasites.brain;

import net.minecraft.init.Blocks;
import net.minecraft.block.Block;

public class BrainConfig {

    // === 🌑 Окружение ===
    public static final int DARKNESS_LIGHT_THRESHOLD = 8;
    public static final int BRIGHT_LIGHT_THRESHOLD = 11;
    public static final int ENV_DECAY_INTERVAL = 40;
    public static final int ENV_RECOVERY_INTERVAL = 220;

    // === 💔 Физика ===
    public static final int HP_LOSS_THRESHOLD = 4;
    public static final int SANITY_PENALTY = 1;
    public static final double MOTION_THRESHOLD = 0.8;
    public static final int EXPLOSION_COOLDOWN = 60;

    // === 💀 Смерть ===
    public static final int SANITY_AFTER_DEATH = 30;
    public static final String[] DEATH_MESSAGES = {
            "%player% решил послушать голоса в голове",
            "%player% не выдержал шёпота в темноте",
            "%player% потерял рассудок окончательно",
            "%player% стал одним из них",
            "%player% не смог бороться с паразитами",
            "%player% растворился в безумии",
            "%player% услышал Зов и не вернулся",
            "%player% больше не человек",
            "Рассудок %player% не выдержал этого мира"
    };

    // === 🚫 Блоки, не считающиеся укрытием ===
    public static boolean isBlockIgnoredForCover(Block block) {
        return block == Blocks.STONE || block == Blocks.COBBLESTONE || block == Blocks.DIRT
                || block == Blocks.GRASS || block == Blocks.GRAVEL || block == Blocks.SAND
                || block == Blocks.SANDSTONE || block == Blocks.COAL_ORE || block == Blocks.IRON_ORE
                || block == Blocks.GOLD_ORE || block == Blocks.DIAMOND_ORE || block == Blocks.EMERALD_ORE
                || block == Blocks.REDSTONE_ORE || block == Blocks.LAPIS_ORE || block == Blocks.QUARTZ_ORE
                || block == Blocks.NETHERRACK || block == Blocks.END_STONE || block == Blocks.OBSIDIAN;
    }

    public static String getRandomDeathMessage(String playerName) {
        String msg = DEATH_MESSAGES[(int)(Math.random() * DEATH_MESSAGES.length)];
        return msg.replace("%player%", playerName);
    }
}