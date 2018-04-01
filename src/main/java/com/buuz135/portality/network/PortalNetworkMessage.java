package com.buuz135.portality.network;

import com.buuz135.portality.data.PortalDataManager;
import com.buuz135.portality.data.PortalInformation;
import com.buuz135.portality.gui.GuiPortals;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PortalNetworkMessage {

    public static class Request implements IMessage {

        private boolean interdimensional;

        public Request() {
        }

        public Request(boolean interdimensional) {
            this.interdimensional = interdimensional;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            interdimensional = buf.readBoolean();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeBoolean(interdimensional);
        }
    }

    public static class Response implements IMessage {

        private List<PortalInformation> information;

        public Response() {
            information = new ArrayList<>();
        }

        public Response(List<PortalInformation> information) {
            this.information = information;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            PacketBuffer packet = new PacketBuffer(buf);
            int amount = packet.readInt();
            while (amount > 0) {
                try {
                    information.add(PortalInformation.readFromNBT(packet.readCompoundTag()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                --amount;
            }
        }

        @Override
        public void toBytes(ByteBuf buf) {
            PacketBuffer packet = new PacketBuffer(buf);
            packet.writeInt(information.size());
            for (PortalInformation portalInformation : information) {
                packet.writeCompoundTag(portalInformation.writetoNBT());
            }
        }
    }

    public static class HandleRequest implements IMessageHandler<Request, Response> {

        @Override
        public Response onMessage(Request message, MessageContext ctx) {
            return new Response(PortalDataManager.getData(ctx.getServerHandler().player.world).getInformationList());
        }

    }

    public static class HandleResponse implements IMessageHandler<Response, IMessage> {

        @Override
        public IMessage onMessage(Response message, MessageContext ctx) {
            if (Minecraft.getMinecraft().currentScreen instanceof GuiPortals) {
                ((GuiPortals) Minecraft.getMinecraft().currentScreen).refresh(message.information);
            }
            return null;
        }
    }
}
