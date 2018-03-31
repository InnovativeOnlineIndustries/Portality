package com.buuz135.portality.network;

import com.buuz135.portality.tile.TileController;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.Charset;

public class PortalRenameMessage implements IMessage {

    private long tileLocation;
    private String name;

    public PortalRenameMessage() {

    }

    public PortalRenameMessage(String name, BlockPos tile) {
        this.name = name;
        this.tileLocation = tile.toLong();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        tileLocation = buf.readLong();
        int length = buf.readInt();
        name = String.valueOf(buf.readCharSequence(length, Charset.defaultCharset()));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(tileLocation);
        buf.writeInt(name.length());
        buf.writeBytes(name.getBytes());
    }

    public static class PortalRenameHandler implements IMessageHandler<PortalRenameMessage, IMessage> {

        @Override
        public IMessage onMessage(PortalRenameMessage message, MessageContext ctx) {
            EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
            serverPlayer.getServerWorld().addScheduledTask(() -> {
                World world = serverPlayer.world;
                BlockPos pos = BlockPos.fromLong(message.tileLocation);
                if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileController) {
                    TileController controller = (TileController) world.getTileEntity(pos);
                    if (controller.getOwner().equals(serverPlayer.getUniqueID())) controller.setName(message.name);
                }
            });
            return null;
        }
    }
}
