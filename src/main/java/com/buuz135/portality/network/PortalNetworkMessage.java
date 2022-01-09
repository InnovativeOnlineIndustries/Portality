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
import com.buuz135.portality.data.TokenPortalInformation;
import com.buuz135.portality.gui.PortalsScreen;
import com.buuz135.portality.tile.ControllerTile;
import com.buuz135.portality.util.BlockPosUtils;
import com.hrznstudio.titanium.network.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PortalNetworkMessage {

    public static void sendInformationToPlayer(ServerPlayer playerEntity, boolean interdimensional, BlockPos pos, int distance, HashMap<String, CompoundTag> tokens) {
        List<PortalInformation> infos = new ArrayList<>();
        tokens.forEach((s, compoundNBT) -> {
            infos.add(new TokenPortalInformation(playerEntity.getUUID(),
                    ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(compoundNBT.getString("Dimension"))),
                    new BlockPos(compoundNBT.getInt("X"), compoundNBT.getInt("Y"), compoundNBT.getInt("Z")),
                    s));
        });
        infos.addAll(PortalDataManager.getData(playerEntity.level).getInformationList());
        infos.removeIf(information -> information.getDimension().equals(playerEntity.getLevel().dimension()) && information.getLocation().equals(pos));
        infos.removeIf(information -> {
            Level world = playerEntity.getServer().getLevel(information.getDimension());
            return world.getBlockEntity(information.getLocation()) instanceof ControllerTile && !((ControllerTile) world.getBlockEntity(information.getLocation())).isFormed();
        });
        infos.removeIf(information -> !interdimensional && !playerEntity.getLevel().dimension().equals(information.getDimension()));
        infos.removeIf(information -> interdimensional && !playerEntity.getLevel().dimension().equals(information.getDimension()) && !information.isInterdimensional());
        infos.removeIf(information -> {
            Level world = playerEntity.getCommandSenderWorld().getServer().getLevel(information.getDimension());
            BlockEntity entity = world.getBlockEntity(information.getLocation());
            return entity instanceof ControllerTile && !interdimensional && (!playerEntity.getLevel().dimension().equals(information.getDimension()) || (!information.getLocation().closerThan(new Vec3(pos.getX(), pos.getY(), pos.getZ()), distance) || !information.getLocation().closerThan(new Vec3(pos.getX(), pos.getY(), pos.getZ()), BlockPosUtils.getMaxDistance(((ControllerTile) entity).getLength()))));
        });
        Portality.NETWORK.get().sendTo(new Response(infos), playerEntity.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static class Response extends Message {

        private CompoundTag compoundNBT;

        public Response() {
            compoundNBT = new CompoundTag();
        }

        public Response(List<PortalInformation> information) {
            compoundNBT = new CompoundTag();
            information.forEach(portalInformation -> compoundNBT.put(portalInformation.getId().toString(), portalInformation.writetoNBT()));
        }

        @Override
        protected void handleMessage(NetworkEvent.Context context) {
            Minecraft.getInstance().tell(() -> {
                if (Minecraft.getInstance().screen instanceof PortalsScreen) {
                    List<PortalInformation> information = new ArrayList<>();
                    compoundNBT.getAllKeys().forEach(s -> information.add(PortalInformation.readFromNBT(compoundNBT.getCompound(s))));
                    ((PortalsScreen) Minecraft.getInstance().screen).refresh(information);
                }
            });
        }

    }
}
