package com.buuz135.portality.proxy;

import com.buuz135.portality.Portality;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Portality.MOD_ID)
public class PortalityConfig {

    @Config.Comment("The amount of energy it will be consumed to teleport an entity")
    @Config.RangeInt(min = 1)
    public static int TELEPORT_ENERGY_AMOUNT = 5000;

    @Config.Comment("If true players will get the wither effect if there isn't enough power to teleport")
    public static boolean HURT_PLAYERS = true;

    @Config.Comment("If true players will be launched out of the portal instead of standing still in front of it")
    public static boolean LAUNCH_PLAYERS = true;

    @Config.Comment("How long the portal structure it can be")
    public static int MAX_PORTAL_LENGTH = 16;

    @Config.Comment("Portal energy buffer")
    @Config.RangeInt(min = 1)
    public static int MAX_PORTAL_POWER = 100000;

    @Config.Comment("Portal energy buffer insertion rate")
    @Config.RangeInt(min = 1)
    public static int MAX_PORTAL_POWER_IN = 2000;

    @Config.Comment("How much power it will be consumed to open the portal interdimensionally")
    @Config.RangeInt(min = 1)
    public static int PORTAL_POWER_OPEN_INTERDIMENSIONAL = 50000;

    @Config.Comment("How much power it will be consumed/tick based on the portal length and if it is the caller. (portalLength*ThisValue). If it is the portal the created the link the power will be double")
    @Config.RangeInt(min = 1)
    public static int POWER_PORTAL_TICK = 1;

    @Config.Comment("Max distance multiplier that a portal can be linked, based on length. PortalLength*ThisValue")
    @Config.RangeInt(min = 1)
    public static int DISTANCE_MULTIPLIER = 200;

    @Mod.EventBusSubscriber(modid = Portality.MOD_ID)
    private static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Portality.MOD_ID)) {
                ConfigManager.sync(Portality.MOD_ID, Config.Type.INSTANCE);
            }
        }
    }
}
