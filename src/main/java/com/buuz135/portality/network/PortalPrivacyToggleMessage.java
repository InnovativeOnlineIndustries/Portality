package com.buuz135.portality.network;

import com.buuz135.portality.tile.TileController;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PortalPrivacyToggleMessage implements IMessage {

    private long tileLocation;

    public PortalPrivacyToggleMessage() {

    }

    public PortalPrivacyToggleMessage(long tileLocation) {
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

    public static class PortalPrivacyToggleHandler implements IMessageHandler<PortalPrivacyToggleMessage, IMessage> {

        @Override
        public IMessage onMessage(PortalPrivacyToggleMessage message, MessageContext ctx) {
            EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
            serverPlayer.getServerWorld().addScheduledTask(() -> {
                World world = serverPlayer.world;
                BlockPos pos = BlockPos.fromLong(message.tileLocation);
                if (world.getTileEntity(pos) instanceof TileController) {
                    TileController controller = (TileController) world.getTileEntity(pos);
                    if (controller.getOwner().equals(serverPlayer.getUniqueID())) controller.togglePrivacy();
                }
            });
            return null;
        }
    }
}
