package com.konqasasas.antimacro;

import com.konqasasas.delaymod.DelayClientCommand;
import com.konqasasas.delaymod.DelayEventHandler;
import com.konqasasas.delaymod.DelayState;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(
        modid = AntiMacroDebugMod.MODID,
        name = AntiMacroDebugMod.NAME,
        version = AntiMacroDebugMod.VERSION,
        clientSideOnly = true,
        acceptedMinecraftVersions = "[1.12.2]"
)
public class AntiMacroDebugMod {
    public static final String MODID = "delaymod";
    public static final String NAME = "Delay Mod";
    public static final String VERSION = "1.0.0";

    private final DelayState delayState = new DelayState();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        this.delayState.load();
        MinecraftForge.EVENT_BUS.register(new DelayEventHandler(this.delayState));
        ClientCommandHandler.instance.registerCommand(new DelayClientCommand(this.delayState));
    }
}
