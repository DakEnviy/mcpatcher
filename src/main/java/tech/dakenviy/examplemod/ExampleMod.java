package tech.dakenviy.examplemod;

import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tech.dakenviy.examplemod.common.CommonProxy;

@Mod(
    modid = ExampleMod.MOD_ID,
    name = ExampleMod.MOD_NAME,
    version = ExampleMod.VERSION,
    acceptedMinecraftVersions = "[@minecraftVersion@]",
    dependencies = "required-after:Forge@[@forgeVersion@,)"
)
public class ExampleMod {

    public static final String MOD_ID = "@modId@";
    public static final String MOD_NAME = "@modName@";
    public static final String VERSION = "@version@";

    @Mod.Instance(MOD_ID)
    public static ExampleMod INSTANCE;

    @SidedProxy(clientSide = "@modGroup@.client.ClientProxy", serverSide = "@modGroup@.common.CommonProxy")
    public static CommonProxy PROXY;

    public static Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        PROXY.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        PROXY.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        PROXY.postInit(event);
    }

    @Mod.EventHandler
    public void serverAboutToStart(FMLServerAboutToStartEvent event) {
        PROXY.serverAboutToStart(event);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        PROXY.serverStarting(event);
    }

    @Mod.EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        PROXY.serverStarted(event);
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        PROXY.serverStopping(event);
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
        PROXY.serverStopped(event);
    }
}
