/*
 * This file is part of Worldgen Indicators.
 *
 * Copyright 2018, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
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

    @Config.Comment("How wide a portal can be without counting the controller(radius)")
    @Config.RangeInt(min = 1)
    public static int MAX_PORTAL_WIDTH = 7;

    @Config.Comment("How tall a portal can be (diameter)")
    @Config.RangeInt(min = 3)
    public static int MAX_PORTAL_HEIGHT = 15;

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
