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
                if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileController) {
                    TileController controller = (TileController) world.getTileEntity(pos);
                    if (controller.getOwner().equals(serverPlayer.getUniqueID()))
                        controller.setDisplayNameEnabled(!controller.isDisplayNameEnabled());
                }
            });
            return null;
        }
    }
}
