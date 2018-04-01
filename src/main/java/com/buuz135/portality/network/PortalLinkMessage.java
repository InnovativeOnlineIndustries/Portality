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
