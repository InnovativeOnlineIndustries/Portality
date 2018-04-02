package com.buuz135.portality.network;

import com.buuz135.portality.tile.TileController;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
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
            new Thread(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Minecraft.getMinecraft().player.playSound(new SoundEvent(new ResourceLocation("entity.shulker.teleport")), 1, 1);
                EnumFacing facing = EnumFacing.values()[message.facing];
                Vec3d vector = new Vec3d(facing.getDirectionVec()).scale(message.length / (double) TileController.MAX_LENGTH);
                EntityPlayerSP player = Minecraft.getMinecraft().player;
                player.motionX = vector.x;
                player.motionY = vector.y;
                player.motionZ = vector.z;
            }).start();
            return null;
        }
    }
}
