package com.buuz135.portality.proxy;

import com.buuz135.portality.Portality;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PortalitySoundHandler {

    public static SoundEvent portal = new SoundEvent(new ResourceLocation(Portality.MOD_ID, "portal")).setRegistryName(new ResourceLocation(Portality.MOD_ID, "portal"));

    @SubscribeEvent
    public void onSoundRegister(RegistryEvent.Register<SoundEvent> registry) {
        registry.getRegistry().register(portal);
    }

}
