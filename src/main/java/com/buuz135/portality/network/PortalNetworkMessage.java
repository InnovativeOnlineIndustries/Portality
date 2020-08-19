/**
 * MIT License
 *
 * Copyright (c) 2018
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.buuz135.portality.network;

import com.buuz135.portality.Portality;
import com.buuz135.portality.data.PortalDataManager;
import com.buuz135.portality.data.PortalInformation;
import com.buuz135.portality.gui.PortalsScreen;
import com.buuz135.portality.tile.ControllerTile;
import com.buuz135.portality.util.BlockPosUtils;
import com.hrznstudio.titanium.network.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;

public class PortalNetworkMessage {

    public static void sendInformationToPlayer(ServerPlayerEntity playerEntity, boolean interdimensional, BlockPos pos, int distance) {
        List<PortalInformation> infos = new ArrayList<>(PortalDataManager.getData(playerEntity.world).getInformationList());
        infos.removeIf(information -> information.getDimension().equals(playerEntity.getServerWorld().getDimensionKey()) && information.getLocation().equals(pos));
        infos.removeIf(information -> {
            World world = playerEntity.getServer().getWorld(information.getDimension());
            return world.getTileEntity(information.getLocation()) instanceof ControllerTile && !((ControllerTile) world.getTileEntity(information.getLocation())).isFormed();
        });
        infos.removeIf(information -> !interdimensional && !playerEntity.getServerWorld().getDimensionKey().equals(information.getDimension()));
        infos.removeIf(information -> interdimensional && !playerEntity.getServerWorld().getDimensionKey().equals(information.getDimension()) && !information.isInterdimensional());
        infos.removeIf(information -> {
            World world = playerEntity.getEntityWorld().getServer().getWorld(information.getDimension());
            TileEntity entity = world.getTileEntity(information.getLocation());
            return entity instanceof ControllerTile && !interdimensional && (!playerEntity.getServerWorld().getDimensionKey().equals(information.getDimension()) || (!information.getLocation().withinDistance(new Vector3d(pos.getX(), pos.getY(), pos.getZ()), distance) || !information.getLocation().withinDistance(new Vector3d(pos.getX(), pos.getY(), pos.getZ()), BlockPosUtils.getMaxDistance(((ControllerTile) entity).getLength()))));
        });
        Portality.NETWORK.get().sendTo(new Response(infos), playerEntity.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static class Response extends Message {

        private CompoundNBT compoundNBT;

        public Response() {
            compoundNBT = new CompoundNBT();
        }

        public Response(List<PortalInformation> information) {
            compoundNBT = new CompoundNBT();
            information.forEach(portalInformation -> compoundNBT.put(portalInformation.getId().toString(), portalInformation.writetoNBT()));
        }

        @Override
        protected void handleMessage(NetworkEvent.Context context) {
            Minecraft.getInstance().enqueue(() -> {
                if (Minecraft.getInstance().currentScreen instanceof PortalsScreen) {
                    List<PortalInformation> information = new ArrayList<>();
                    compoundNBT.keySet().forEach(s -> information.add(PortalInformation.readFromNBT(compoundNBT.getCompound(s))));
                    ((PortalsScreen) Minecraft.getInstance().currentScreen).refresh(information);
                }
            });
        }

    }
}
