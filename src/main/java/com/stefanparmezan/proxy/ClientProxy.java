package com.stefanparmezan.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

import java.util.Objects;

public class ClientProxy extends CommonProxy {
    public void registerItemRenderer(Item item, int metaData, String id){
        ModelLoader.setCustomModelResourceLocation(item, metaData, new ModelResourceLocation(Objects.requireNonNull(item.getRegistryName()), id));
    }
}
