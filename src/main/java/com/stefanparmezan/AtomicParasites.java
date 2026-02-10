package com.stefanparmezan;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = AtomicParasitesInfo.MOD_ID, name = AtomicParasitesInfo.NAME, version = AtomicParasitesInfo.VERSION
)
public class AtomicParasites {


	
	public static final Logger LOGGER = LogManager.getLogger(AtomicParasitesInfo.MOD_ID);

    @Mod.EventHandler
	public void preinit(FMLPreInitializationEvent preinit) {
		LOGGER.info("Hello, world!");
	}
}
