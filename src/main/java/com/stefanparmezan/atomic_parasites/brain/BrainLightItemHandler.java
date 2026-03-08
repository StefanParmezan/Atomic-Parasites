package com.stefanparmezan.atomic_parasites.brain;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class BrainLightItemHandler {

    public static boolean hasLightItem(AbstractClientPlayer player) {
        return getLightLevel(player) >= BrainConfig.ITEM_LIGHT_MIN_LEVEL;
    }

    public static int getLightLevel(AbstractClientPlayer player) {
        ItemStack mainHand = player.getHeldItemMainhand();
        ItemStack offHand = player.getHeldItemOffhand();
        return Math.max(getLightLevelFromItem(mainHand), getLightLevelFromItem(offHand));
    }

    private static int getLightLevelFromItem(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        Item item = stack.getItem();
        Block block = Block.getBlockFromItem(item);

        if (block != null && block != Blocks.AIR) {
            int lightLevel = block.getDefaultState().getLightValue();
            if (lightLevel > 0) return lightLevel;
        }

        String registryName = item.getRegistryName().toString().toLowerCase();
        String displayName = stack.getDisplayName().toLowerCase();
        if (containsLightKeyword(registryName) || containsLightKeyword(displayName)) {
            return 12;
        }
        return 0;
    }

    private static boolean containsLightKeyword(String text) {
        String[] keywords = {"torch", "lantern", "light", "glow", "lamp", "beacon",
                "luminescent", "radiant", "bright", "phosphor", "flame", "fire"};
        for (String keyword : keywords) {
            if (text.contains(keyword)) return true;
        }
        return false;
    }

    public static String getHeldLightItemName(AbstractClientPlayer player) {
        ItemStack mainHand = player.getHeldItemMainhand();
        ItemStack offHand = player.getHeldItemOffhand();
        int mainLevel = getLightLevelFromItem(mainHand);
        int offLevel = getLightLevelFromItem(offHand);
        if (mainLevel >= BrainConfig.ITEM_LIGHT_MIN_LEVEL)
            return mainHand.getDisplayName() + " (L:" + mainLevel + ")";
        if (offLevel >= BrainConfig.ITEM_LIGHT_MIN_LEVEL)
            return offHand.getDisplayName() + " (L:" + offLevel + ")";
        return "Нет";
    }

    public static int getMultiplierForItem(ItemStack stack) {
        int lightLevel = getLightLevelFromItem(stack);
        if (lightLevel < BrainConfig.ITEM_LIGHT_MIN_LEVEL) return 1;
        if (lightLevel >= BrainConfig.ITEM_LIGHT_HIGH_THRESHOLD) return BrainConfig.ITEM_LIGHT_MULTIPLIER_HIGH;
        return BrainConfig.ITEM_LIGHT_MULTIPLIER_LOW;
    }
}