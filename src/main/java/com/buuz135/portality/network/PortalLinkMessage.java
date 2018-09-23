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

import com.buuz135.portality.data.PortalLinkData;
import com.buuz135.portality.tile.TileController;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.IOException;

public class PortalLinkMessage implements IMessage {

    private int type;
    private PortalLinkData linkSender;
    private PortalLinkData linkReceiver;

    public PortalLinkMessage() {
    }

    public PortalLinkMessage(int type, PortalLinkData linkSender, PortalLinkData linkReceiver) {
        this.type = type;
        this.linkSender = linkSender;
        this.linkReceiver = linkReceiver;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer packet = new PacketBuffer(buf);
        type = packet.readInt();
        try {
            linkSender = PortalLinkData.readFromNBT(packet.readCompoundTag());
            linkReceiver = PortalLinkData.readFromNBT(packet.readCompoundTag());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer packet = new PacketBuffer(buf);
        packet.writeInt(type);
        packet.writeCompoundTag(linkSender.writeToNBT());
        packet.writeCompoundTag(linkReceiver.writeToNBT());
    }

    public static class Handler implements IMessageHandler<PortalLinkMessage, IMessage> {

        @Override
        public IMessage onMessage(PortalLinkMessage message, MessageContext ctx) {
            World world = ctx.getServerHandler().player.world.getMinecraftServer().getWorld(message.linkSender.getDimension());
            TileEntity tileEntity = world.getTileEntity(message.linkSender.getPos());
            if (tileEntity instanceof TileController) {
                ((TileController) tileEntity).linkTo(new PortalLinkData(message.linkReceiver.getDimension(), message.linkReceiver.getPos(), true), PortalLinkData.PortalCallType.values()[message.type]);
            }
            return null;
        }
    }
}
