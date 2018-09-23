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

import com.buuz135.portality.tile.TileController;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PortalDisplayToggleMessage implements IMessage {

    private long tileLocation;

    public PortalDisplayToggleMessage() {

    }

    public PortalDisplayToggleMessage(long tileLocation) {
        this.tileLocation = tileLocation;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        tileLocation = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(tileLocation);
    }

    public static class Handler implements IMessageHandler<PortalDisplayToggleMessage, IMessage> {

        @Override
        public IMessage onMessage(PortalDisplayToggleMessage message, MessageContext ctx) {
            EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
            serverPlayer.getServerWorld().addScheduledTask(() -> {
                World world = serverPlayer.world;
                BlockPos pos = BlockPos.fromLong(message.tileLocation);
                if (world.getTileEntity(pos) instanceof TileController) {
                    TileController controller = (TileController) world.getTileEntity(pos);
                    if (controller.getOwner().equals(serverPlayer.getUniqueID()))
                        controller.setDisplayNameEnabled(!controller.isDisplayNameEnabled());
                }
            });
            return null;
        }
    }
}
