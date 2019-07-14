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

import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.config.ConfigVal;

@ConfigFile()
public class PortalityConfig {

    @ConfigVal(comment = "The amount of energy it will be consumed to teleport an entity")
    @ConfigVal.InRangeInt(min = 1)
    public static int TELEPORT_ENERGY_AMOUNT = 5000;

    @ConfigVal(comment = "If true players will get the wither effect if there isn't enough power to teleport")
    public static boolean HURT_PLAYERS = true;

    @ConfigVal(comment = "If true players will be launched out of the portal instead of standing still in front of it")
    public static boolean LAUNCH_PLAYERS = true;

    @ConfigVal(comment = "How long the portal structure it can be")
    public static int MAX_PORTAL_LENGTH = 16;

    @ConfigVal(comment = "How wide a portal can be without counting the controller(radius)")
    @ConfigVal.InRangeInt(min = 1)
    public static int MAX_PORTAL_WIDTH = 7;

    @ConfigVal(comment = "How tall a portal can be (diameter)")
    @ConfigVal.InRangeInt(min = 3)
    public static int MAX_PORTAL_HEIGHT = 15;

    @ConfigVal(comment = "Portal energy buffer")
    @ConfigVal.InRangeInt(min = 1)
    public static int MAX_PORTAL_POWER = 100000;

    @ConfigVal(comment = "Portal energy buffer insertion rate")
    @ConfigVal.InRangeInt(min = 1)
    public static int MAX_PORTAL_POWER_IN = 2000;

    @ConfigVal(comment = "How much power it will be consumed to open the portal interdimensionally")
    @ConfigVal.InRangeInt(min = 1)
    public static int PORTAL_POWER_OPEN_INTERDIMENSIONAL = 50000;

    @ConfigVal(comment = "How much power it will be consumed/tick based on the portal length and if it is the caller. (portalLength*ThisValue). If it is the portal the created the link the power will be double")
    @ConfigVal.InRangeInt(min = 1)
    public static int POWER_PORTAL_TICK = 1;

    @ConfigVal(comment = "Max distance multiplier that a portal can be linked, based on length. PortalLength*ThisValue")
    @ConfigVal.InRangeInt(min = 1)
    public static int DISTANCE_MULTIPLIER = 200;


}
