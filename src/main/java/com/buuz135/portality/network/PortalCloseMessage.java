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

public class PortalCloseMessage implements IMessage {

    private PortalLinkData data;

    public PortalCloseMessage(PortalLinkData data) {
        this.data = data;
    }

    public PortalCloseMessage() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        try {
            data = PortalLinkData.readFromNBT(buffer.readCompoundTag());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeCompoundTag(data.writeToNBT());
    }

    public static class Handler implements IMessageHandler<PortalCloseMessage, IMessage> {

        @Override
        public IMessage onMessage(PortalCloseMessage message, MessageContext ctx) {
            World world = ctx.getServerHandler().player.world.getMinecraftServer().getWorld(message.data.getDimension());
            TileEntity tileEntity = world.getTileEntity(message.data.getPos());
            if (tileEntity instanceof TileController) {
                ((TileController) tileEntity).closeLink();
            }
            return null;
        }
    }
}
