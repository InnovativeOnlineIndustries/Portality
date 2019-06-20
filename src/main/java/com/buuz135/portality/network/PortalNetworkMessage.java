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

import com.buuz135.portality.data.PortalDataManager;
import com.buuz135.portality.data.PortalInformation;
import com.buuz135.portality.tile.TileController;
import com.buuz135.portality.util.BlockPosUtils;
import com.hrznstudio.titanium.network.Message;
import com.hrznstudio.titanium.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;

public class PortalNetworkMessage {

    public static class Request extends Message {

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
        protected void handleMessage(NetworkEvent.Context context) {
            context.enqueueWork(() -> {
                List<PortalInformation> infos = new ArrayList<>(PortalDataManager.getData(context.getSender().world).getInformationList());
                infos.removeIf(information -> {
                    World world = context.getSender().getServer().getWorld(DimensionType.byName(information.getDimension()));
                    return world.getTileEntity(information.getLocation()) instanceof TileController && !((TileController) world.getTileEntity(information.getLocation())).isFormed();
                });
                infos.removeIf(information -> !interdimensional && !context.getSender().getServerWorld().getDimension().getType().getRegistryName().equals(information.getDimension()));
                infos.removeIf(information -> interdimensional && !context.getSender().getServerWorld().getDimension().getType().getRegistryName().equals(information.getDimension()) && !information.isInterdimensional());
                infos.removeIf(information -> {
                    World world = context.getSender().world.getServer().getWorld(DimensionType.byName(information.getDimension()));
                    TileEntity entity = world.getTileEntity(information.getLocation());
                    return entity instanceof TileController && !interdimensional && context.getSender().getServerWorld().getDimension().getType().getRegistryName().equals(information.getDimension()) && (information.getLocation().distanceSq(new Vec3i(pos.getX(), pos.getY(), pos.getZ())) >= distance || information.getLocation().distanceSq(new Vec3i(pos.getX(), pos.getY(), pos.getZ())) >= BlockPosUtils.getMaxDistance(((TileController) entity).getLength()));
                });
                NetworkHandler.NETWORK.sendTo(new Response(infos), context.getSender().connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
            });
        }

    }

    public static class Response extends Message {

        private List<PortalInformation> information;

        public Response() {
            information = new ArrayList<>();
        }

        public Response(List<PortalInformation> information) {
            this.information = information;
        }

        @Override
        protected void handleMessage(NetworkEvent.Context context) {
            Minecraft.getInstance().enqueue(() -> {
//                if (Minecraft.getInstance().currentScreen instanceof GuiPortals) {
//                    ((GuiPortals) Minecraft.getInstance().currentScreen).refresh(information);
//                }
            });
        }

    }
}
