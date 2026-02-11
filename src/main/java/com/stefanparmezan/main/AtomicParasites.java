package com.stefanparmezan.main;

import com.stefanparmezan.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = AtomicParasitesInfo.MOD_ID, name = AtomicParasitesInfo.NAME, version = AtomicParasitesInfo.VERSION
)
public class AtomicParasites {


	public static final Logger LOGGER = LogManager.getLogger(AtomicParasitesInfo.MOD_ID);

    @Mod.Instance
    public static AtomicParasites instance;

    @SidedProxy(clientSide = AtomicParasitesInfo.CLIENT, serverSide = AtomicParasitesInfo.COMMON)
    public static CommonProxy proxy;

    @Mod.EventHandler
	public void preinit(FMLPreInitializationEvent preinit) {
		LOGGER.info("\\u001B[34m" + "Started preInit");
	}

    @Mod.EventHandler
    public static void init(FMLInitializationEvent init){
        LOGGER.info("\\u001B[34m" + "Started init");
    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent postInit){
        LOGGER.info("\\u001B[34m" + "Started postInit");
    }
}
