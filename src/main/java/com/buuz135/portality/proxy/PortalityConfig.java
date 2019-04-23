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


import net.minecraftforge.common.ForgeConfigSpec;

public class PortalityConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static Common COMMON = new Common();

    public static class Common {
        public ForgeConfigSpec.ConfigValue<Integer> TELEPORT_ENERGY_AMOUNT;
        public ForgeConfigSpec.ConfigValue<Boolean> HURT_PLAYERS;
        public ForgeConfigSpec.ConfigValue<Boolean> LAUNCH_PLAYERS;
        public ForgeConfigSpec.ConfigValue<Integer> MAX_PORTAL_LENGTH;
        public ForgeConfigSpec.ConfigValue<Integer> MAX_PORTAL_WIDTH;
        public ForgeConfigSpec.ConfigValue<Integer> MAX_PORTAL_HEIGHT;
        public ForgeConfigSpec.ConfigValue<Integer> MAX_PORTAL_POWER;
        public ForgeConfigSpec.ConfigValue<Integer> MAX_PORTAL_POWER_IN;
        public ForgeConfigSpec.ConfigValue<Integer> PORTAL_POWER_OPEN_INTERDIMENSIONAL;
        public ForgeConfigSpec.ConfigValue<Integer> POWER_PORTAL_TICK;
        public ForgeConfigSpec.ConfigValue<Integer> DISTANCE_MULTIPLIER;

        Common() {
            BUILDER.push("COMMON");

            TELEPORT_ENERGY_AMOUNT = BUILDER.comment("The amount of energy it will be consumed to teleport an entity")
                    .defineInRange("TELEPORT_ENERGY_AMOUNT", 5000, 1, Integer.MAX_VALUE);
            HURT_PLAYERS = BUILDER.comment("If true players will get the wither effect if there isn't enough power to teleport")
                    .define("HURT_PLAYERS", true);
            LAUNCH_PLAYERS = BUILDER.comment("If true players will be launched out of the portal instead of standing still in front of it")
                    .define("LAUNCH_PLAYERS", true);
            MAX_PORTAL_LENGTH = BUILDER.comment("How long the portal structure it can be")
                    .defineInRange("MAX_PORTAL_LENGTH", 16, 3, Integer.MAX_VALUE);
            MAX_PORTAL_WIDTH = BUILDER.comment("How wide a portal can be without counting the controller(radius)")
                    .defineInRange("MAX_PORTAL_WIDTH", 16, 1, Integer.MAX_VALUE);
            MAX_PORTAL_HEIGHT = BUILDER.comment("How tall a portal can be (diameter)")
                    .defineInRange("MAX_PORTAL_HEIGHT", 16, 3, Integer.MAX_VALUE);
            MAX_PORTAL_POWER = BUILDER.comment("Portal energy buffer")
                    .defineInRange("MAX_PORTAL_POWER", 100000, 1, Integer.MAX_VALUE);
            MAX_PORTAL_POWER_IN = BUILDER.comment("Portal energy buffer insertion rate")
                    .defineInRange("MAX_PORTAL_POWER_IN", 2000, 1, Integer.MAX_VALUE);
            PORTAL_POWER_OPEN_INTERDIMENSIONAL = BUILDER.comment("How much power it will be consumed to open the portal interdimensionally")
                    .defineInRange("PORTAL_POWER_OPEN_INTERDIMENSIONAL", 50000, 1, Integer.MAX_VALUE);
            POWER_PORTAL_TICK = BUILDER.comment("How much power it will be consumed/tick based on the portal length and if it is the caller. (portalLength*ThisValue). If it is the portal the created the link the power will be double")
                    .defineInRange("POWER_PORTAL_TICK", 1, 1, Integer.MAX_VALUE);
            DISTANCE_MULTIPLIER = BUILDER.comment("Max distance multiplier that a portal can be linked, based on length. PortalLength*ThisValue")
                    .defineInRange("DISTANCE_MULTIPLIER ", 200, 1, Integer.MAX_VALUE);

            BUILDER.pop();
        }
    }


}
