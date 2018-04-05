package com.buuz135.portality.network;

import com.buuz135.portality.Portality;
import com.buuz135.portality.data.PortalDataManager;
import com.buuz135.portality.data.PortalInformation;
import com.buuz135.portality.gui.GuiPortals;
import com.buuz135.portality.tile.TileController;
import com.buuz135.portality.util.BlockPosUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PortalNetworkMessage {

    public static class Request implements IMessage {

        private boolean interdimensional;
        private BlockPos pos;
        private int distance;

        public Request() {
        }

        public Request(boolean interdimensional, BlockPos pos, int distance) {
            this.interdimensional = interdimensional;
            this.pos = pos;
            this.distance = distance;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            interdimensional = buf.readBoolean();
            pos = BlockPos.fromLong(buf.readLong());
            distance = buf.readInt();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeBoolean(interdimensional);
            buf.writeLong(pos.toLong());
            buf.writeInt(distance);
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
            ctx.getServerHandler().player.getServer().addScheduledTask(() -> {
                List<PortalInformation> infos = new ArrayList<>(PortalDataManager.getData(ctx.getServerHandler().player.world).getInformationList());
                infos.removeIf(information -> {
                    World world = ctx.getServerHandler().player.getServer().getWorld(information.getDimension());
                    return world.getTileEntity(information.getLocation()) instanceof TileController && !((TileController) world.getTileEntity(information.getLocation())).isFormed();
                });
                infos.removeIf(information -> !message.interdimensional && ctx.getServerHandler().player.getServerWorld().provider.getDimension() != information.getDimension());
                infos.removeIf(information -> message.interdimensional && ctx.getServerHandler().player.getServerWorld().provider.getDimension() != information.getDimension() && !information.isInterdimensional());
                infos.removeIf(information -> {
                    World world = ctx.getServerHandler().player.world.getMinecraftServer().getWorld(information.getDimension());
                    TileEntity entity = world.getTileEntity(information.getLocation());
                    return entity instanceof TileController && !message.interdimensional && ctx.getServerHandler().player.getServerWorld().provider.getDimension() == information.getDimension() && (information.getLocation().getDistance(message.pos.getX(), message.pos.getY(), message.pos.getZ()) >= message.distance || information.getLocation().getDistance(message.pos.getX(), message.pos.getY(), message.pos.getZ()) >= BlockPosUtils.getMaxDistance(((TileController) entity).getLength()));
                });
                Portality.NETWORK.sendTo(new Response(infos), ctx.getServerHandler().player);
            });
            return null;
        }

    }

    public static class HandleResponse implements IMessageHandler<Response, IMessage> {

        @Override
        public IMessage onMessage(Response message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if (Minecraft.getMinecraft().currentScreen instanceof GuiPortals) {
                    ((GuiPortals) Minecraft.getMinecraft().currentScreen).refresh(message.information);
                }
            });
            return null;
        }
    }
}
