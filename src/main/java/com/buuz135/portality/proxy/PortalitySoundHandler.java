package com.buuz135.portality.proxy;

import com.buuz135.portality.Portality;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PortalitySoundHandler {

    public static final SoundEvent PORTAL = new SoundEvent(new ResourceLocation(Portality.MOD_ID, "portal")).setRegistryName(new ResourceLocation(Portality.MOD_ID, "portal"));
    public static final SoundEvent PORTAL_TP = new SoundEvent(new ResourceLocation(Portality.MOD_ID, "portal_teleport")).setRegistryName(new ResourceLocation(Portality.MOD_ID, "portal_teleport"));


    @SubscribeEvent
    public void onSoundRegister(RegistryEvent.Register<SoundEvent> registry) {
        registry.getRegistry().registerAll(PORTAL, PORTAL_TP);
    }

}
