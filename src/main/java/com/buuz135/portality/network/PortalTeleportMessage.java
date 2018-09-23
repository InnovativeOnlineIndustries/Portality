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
package com.buuz135.portality.network;

import com.buuz135.portality.proxy.PortalityConfig;
import com.buuz135.portality.proxy.PortalitySoundHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PortalTeleportMessage implements IMessage {

    private int facing;
    private int length;

    public PortalTeleportMessage(int facing, int length) {
        this.facing = facing;
        this.length = length;
    }

    public PortalTeleportMessage() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        facing = buf.readInt();
        length = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(facing);
        buf.writeInt(length);
    }

    public static class Handler implements IMessageHandler<PortalTeleportMessage, IMessage> {

        @Override
        public IMessage onMessage(PortalTeleportMessage message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                //Minecraft.getMinecraft().player.playSound(new SoundEvent(new ResourceLocation("entity.shulker.teleport")), 1, 1);
                Minecraft.getMinecraft().player.playSound(PortalitySoundHandler.PORTAL_TP, 0.1f, 1f);
                if (PortalityConfig.LAUNCH_PLAYERS) {
                    EnumFacing facing = EnumFacing.values()[message.facing];
                    Vec3d vector = new Vec3d(facing.getDirectionVec()).scale(2 * message.length / (double) PortalityConfig.MAX_PORTAL_LENGTH);
                    EntityPlayerSP player = Minecraft.getMinecraft().player;
                    player.motionX = vector.x;
                    player.motionY = vector.y;
                    player.motionZ = vector.z;
                }
            });
            return null;
        }
    }
}
