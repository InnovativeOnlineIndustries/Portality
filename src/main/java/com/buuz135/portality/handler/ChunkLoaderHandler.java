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
package com.buuz135.portality.handler;

import com.buuz135.portality.Portality;
import com.buuz135.portality.tile.TileController;
import com.google.common.collect.ImmutableSet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkLoaderHandler implements ForgeChunkManager.LoadingCallback {

    public static final HashMap<TileController, ForgeChunkManager.Ticket> tickets = new HashMap<>();

    public static void removePortalAsChunkloader(TileController controller) {
        ForgeChunkManager.Ticket ticket = tickets.get(controller);
        if (ticket != null) {
            ForgeChunkManager.releaseTicket(ticket);
        }
        tickets.remove(controller);

    }

    public static void addPortalAsChunkloader(TileController controller) {
        if (controller != null) {
            ChunkPos chunkCoordIntPair = new ChunkPos(controller.getPos().getX() >> 4, controller.getPos().getZ() >> 4);
            if (!isAlreadyChunkLoaded(chunkCoordIntPair, controller.getWorld().provider.getDimension())) {
                ForgeChunkManager.Ticket ticket = tickets.get(controller);
                if (ticket == null) {
                    ticket = ForgeChunkManager.requestTicket(Portality.INSTANCE, controller.getWorld(), ForgeChunkManager.Type.NORMAL);
                }
                if (ticket != null) {
                    ticket.getModData().setInteger("X", controller.getPos().getX());
                    ticket.getModData().setInteger("Y", controller.getPos().getY());
                    ticket.getModData().setInteger("Z", controller.getPos().getZ());
                    ForgeChunkManager.forceChunk(ticket, chunkCoordIntPair);
                }
                tickets.put(controller, ticket);
            }
        }
    }

    public static boolean isAlreadyChunkLoaded(TileController dualVertical) {
        ChunkPos chunkCoordIntPair = new ChunkPos(dualVertical.getPos().getX() >> 4, dualVertical.getPos().getZ() >> 4);
        return tickets.containsKey(dualVertical) || isAlreadyChunkLoaded(chunkCoordIntPair, dualVertical.getWorld().provider.getDimension());
    }

    public static boolean isAlreadyChunkLoaded(ChunkPos chunkCoordIntPair, int dimID) {
        for (Map.Entry<TileController, ForgeChunkManager.Ticket> set : tickets.entrySet()) {
            if (set != null && set.getValue() != null) {
                ImmutableSet<ChunkPos> loadedChunks = set.getValue().getChunkList();
                if (loadedChunks != null && set.getValue().world.provider.getDimension() == dimID) {
                    for (ChunkPos theChunks : loadedChunks) {
                        if (theChunks.equals(chunkCoordIntPair)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, World world) {
        for (ForgeChunkManager.Ticket ticket : tickets) {
            if (ticket != null) {
                TileEntity te = world.getTileEntity(new BlockPos(ticket.getModData().getInteger("X"), ticket.getModData().getInteger("Y"), ticket.getModData().getInteger("Z")));
                if (te instanceof TileController) {
                    TileController dv = (TileController) te;
                    ForgeChunkManager.Ticket ticket1 = ChunkLoaderHandler.tickets.get(dv);
                    if (ticket1 != null) {
                        ForgeChunkManager.releaseTicket(ticket1);
                    }
                    ChunkLoaderHandler.tickets.put(dv, ticket);
                    ForgeChunkManager.forceChunk(ticket, new ChunkPos(dv.getPos().getX() >> 4, dv.getPos().getZ() >> 4));
                } else {
                    ForgeChunkManager.releaseTicket(ticket);
                }
            }
        }
    }
}
