package com.stefanparmezan.atomic_parasites.brain;

import com.stefanparmezan.atomic_parasites.main.AtomicParasites;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class BrainLightItemHandler {

    // === 🌟 МИНИМАЛЬНЫЙ УРОВЕНЬ СВЕТА ДЛЯ ЗАЩИТЫ ===
    public static final int MIN_LIGHT_LEVEL = 10;

    // === 🔍 ПРОВЕРКА: есть ли подходящий светящийся предмет в руках ===
    public static boolean hasLightItem(AbstractClientPlayer player) {
        return getLightLevel(player) >= MIN_LIGHT_LEVEL;
    }

    // === 💡 ПОЛУЧИТЬ УРОВЕНЬ СВЕТА ПРЕДМЕТА (АВТО-ДЕТЕКЦИЯ) ===
    public static int getLightLevel(AbstractClientPlayer player) {
        ItemStack mainHand = player.getHeldItemMainhand();
        ItemStack offHand = player.getHeldItemOffhand();

        int mainLevel = getLightLevelFromItem(mainHand);
        int offLevel = getLightLevelFromItem(offHand);

        return Math.max(mainLevel, offLevel);
    }

    // === 🔍 АВТОМАТИЧЕСКОЕ ОПРЕДЕЛЕНИЕ СВЕТА ПРЕДМЕТА ===
    private static int getLightLevelFromItem(ItemStack stack) {
        if (stack.isEmpty()) return 0;

        Item item = stack.getItem();

        // 1️⃣ ПРЯМАЯ ПРОВЕРКА: Получаем блок из предмета
        Block block = Block.getBlockFromItem(item);

        if (block != null && block != Blocks.AIR) {
            // Получаем уровень света блока (0-15)
            int lightLevel = block.getDefaultState().getLightValue();
            if (lightLevel > 0) {
                return lightLevel;
            }
        }

        // 2️⃣ РЕЗЕРВ: Проверка по названию (для предметов без блока)
        String registryName = item.getRegistryName().toString().toLowerCase();
        String displayName = stack.getDisplayName().toLowerCase();

        if (containsLightKeyword(registryName) || containsLightKeyword(displayName)) {
            // Дефолтный свет для модовых предметов, которые светятся, но не имеют блока
            return 12;
        }

        return 0;
    }

    // === 🔍 ПОИСК КЛЮЧЕВЫХ СЛОВ В НАЗВАНИИ ===
    private static boolean containsLightKeyword(String text) {
        String[] keywords = {
                "torch", "lantern", "light", "glow", "lamp", "beacon",
                "luminescent", "radiant", "bright", "phosphor", "flame", "fire"
        };
        for (String keyword : keywords) {
            if (text.contains(keyword)) return true;
        }
        return false;
    }

    // === 📊 ПОЛУЧИТЬ НАЗВАНИЕ ПРЕДМЕТА ДЛЯ ЛОГА ===
    public static String getHeldLightItemName(AbstractClientPlayer player) {
        ItemStack mainHand = player.getHeldItemMainhand();
        ItemStack offHand = player.getHeldItemOffhand();

        int mainLevel = getLightLevelFromItem(mainHand);
        int offLevel = getLightLevelFromItem(offHand);

        if (mainLevel >= MIN_LIGHT_LEVEL)
            return mainHand.getDisplayName() + " (L:" + mainLevel + ")";
        if (offLevel >= MIN_LIGHT_LEVEL)
            return offHand.getDisplayName() + " (L:" + offLevel + ")";
        return "Нет";
    }

    // === 📊 ПОЛУЧИТЬ МНОЖИТЕЛЬ ДЛЯ ПРЕДМЕТА ===
    public static int getMultiplierForItem(ItemStack stack) {
        int lightLevel = getLightLevelFromItem(stack);
        if (lightLevel < MIN_LIGHT_LEVEL) return 1;
        if (lightLevel >= 13) return 3;  // x3 для 13-15
        return 2;  // x2 для 10-12
    }
}